package de.uniks.networkparser.bytes.qr;

/**
 * <p>
 * Represents a polynomial whose coefficients are elements of a GF. Instances of this class are
 * immutable.
 * </p>
 *
 * <p>
 * Much credit is due to William Rucklidge since portions of this code are an indirect port of his
 * C++ Reed-Solomon implementation.
 * </p>
 *
 * @author Sean Owen
 */
public final class GenericGFPoly {

  private GenericGF field;
  private int[] coefficients;

  public GenericGFPoly() {

  }

  /**
   * @param field the {@link GenericGF} instance representing the field to use to perform computations
   * @param coefficients coefficients as ints representing elements of GF(size), arranged from most
   *        significant (highest-power term) coefficient to least significant
   */
  GenericGFPoly(GenericGF field, int[] coefficients) {
    if (coefficients == null || coefficients.length == 0) {
      return;
    }
    this.field = field;
    int coefficientsLength = coefficients.length;
    if (coefficientsLength > 1 && coefficients[0] == 0) {
      /*
       * Leading term must be non-zero for anything except the constant polynomial "0"
       */
      int firstNonZero = 1;
      while (firstNonZero < coefficientsLength && coefficients[firstNonZero] == 0) {
        firstNonZero++;
      }
      if (firstNonZero == coefficientsLength) {
        this.coefficients = new int[] {0};
      } else {
        this.coefficients = new int[coefficientsLength - firstNonZero];
        System.arraycopy(coefficients, firstNonZero, this.coefficients, 0, this.coefficients.length);
      }
    } else {
      this.coefficients = coefficients;
    }
  }

  int[] getCoefficients() {
    return coefficients;
  }

  /**
   * @return degree of this polynomial
   */
  int getDegree() {
    if (coefficients == null) {
      return 0;
    }
    return coefficients.length - 1;
  }

  /**
   * @return true iff this polynomial is the monomial "0"
   */
  boolean isZero() {
    return coefficients != null && coefficients[0] == 0;
  }

  /**
   * @param degree degree
   * @return coefficient of x^degree term in this polynomial
   */
  int getCoefficient(int degree) {
    if (coefficients == null) {
      return 0;
    }
    return coefficients[coefficients.length - 1 - degree];
  }

  /**
   * @param a polynomial
   * @return evaluation of this polynomial at a given point
   */
  int evaluateAt(int a) {
    if (a == 0 || coefficients == null) {
      /* Just return the x^0 coefficient */
      return getCoefficient(0);
    }
    int size = coefficients.length;
    if (a == 1) {
      /* Just the sum of the coefficients */
      int result = 0;
      for (int coefficient : coefficients) {
        result = GenericGF.addOrSubtract(result, coefficient);
      }
      return result;
    }
    int result = coefficients[0];
    for (int i = 1; i < size; i++) {
      result = GenericGF.addOrSubtract(field.multiply(a, result), coefficients[i]);
    }
    return result;
  }

  GenericGFPoly addOrSubtract(GenericGFPoly other) {
    if (field == null || !field.equals(other.field)) {
      return null;
    }
    if (isZero()) {
      return other;
    }
    if (other.isZero()) {
      return this;
    }

    int[] smallerCoefficients = this.coefficients;
    int[] largerCoefficients = other.coefficients;
    if (smallerCoefficients.length > largerCoefficients.length) {
      int[] temp = smallerCoefficients;
      smallerCoefficients = largerCoefficients;
      largerCoefficients = temp;
    }
    int[] sumDiff = new int[largerCoefficients.length];
    int lengthDiff = largerCoefficients.length - smallerCoefficients.length;
    /*
     * Copy high-order terms only found in higher-degree polynomial's coefficients
     */
    System.arraycopy(largerCoefficients, 0, sumDiff, 0, lengthDiff);

    for (int i = lengthDiff; i < largerCoefficients.length; i++) {
      sumDiff[i] = GenericGF.addOrSubtract(smallerCoefficients[i - lengthDiff], largerCoefficients[i]);
    }

    return new GenericGFPoly(field, sumDiff);
  }

  GenericGFPoly multiply(GenericGFPoly other) {
    if (field == null || !field.equals(other.field)) {
      return null;
    }
    if (isZero() || other.isZero()) {
      return field.getZero();
    }
    int[] aCoefficients = this.coefficients;
    int aLength = aCoefficients.length;
    int[] bCoefficients = other.coefficients;
    int bLength = bCoefficients.length;
    int[] product = new int[aLength + bLength - 1];
    for (int i = 0; i < aLength; i++) {
      int aCoeff = aCoefficients[i];
      for (int j = 0; j < bLength; j++) {
        product[i + j] = GenericGF.addOrSubtract(product[i + j], field.multiply(aCoeff, bCoefficients[j]));
      }
    }
    return new GenericGFPoly(field, product);
  }

  GenericGFPoly multiply(int scalar) {
    if (field == null) {
      return this;
    }
    if (scalar == 0) {
      return field.getZero();
    }
    if (scalar == 1) {
      return this;
    }
    int size = coefficients.length;
    int[] product = new int[size];
    for (int i = 0; i < size; i++) {
      product[i] = field.multiply(coefficients[i], scalar);
    }
    return new GenericGFPoly(field, product);
  }

  GenericGFPoly multiplyByMonomial(int degree, int coefficient) {
    if (degree < 0 || field == null) {
      return null;
    }
    if (coefficient == 0) {
      return field.getZero();
    }
    int size = coefficients.length;
    int[] product = new int[size + degree];
    for (int i = 0; i < size; i++) {
      product[i] = field.multiply(coefficients[i], coefficient);
    }
    return new GenericGFPoly(field, product);
  }

  GenericGFPoly[] divide(GenericGFPoly other) {
    if (field == null || !field.equals(other.field)) {
      return null;
    }
    if (other.isZero()) {
      return null;
    }

    GenericGFPoly quotient = field.getZero();
    GenericGFPoly remainder = this;

    int denominatorLeadingTerm = other.getCoefficient(other.getDegree());
    int inverseDenominatorLeadingTerm = field.inverse(denominatorLeadingTerm);

    while (remainder.getDegree() >= other.getDegree() && !remainder.isZero()) {
      int degreeDifference = remainder.getDegree() - other.getDegree();
      int scale = field.multiply(remainder.getCoefficient(remainder.getDegree()), inverseDenominatorLeadingTerm);
      GenericGFPoly term = other.multiplyByMonomial(degreeDifference, scale);
      GenericGFPoly iterationQuotient = field.buildMonomial(degreeDifference, scale);
      quotient = quotient.addOrSubtract(iterationQuotient);
      remainder = remainder.addOrSubtract(term);
    }

    return new GenericGFPoly[] {quotient, remainder};
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder(8 * getDegree());
    for (int degree = getDegree(); degree >= 0; degree--) {
      int coefficient = getCoefficient(degree);
      if (coefficient != 0) {
        if (coefficient < 0) {
          result.append(" - ");
          coefficient = -coefficient;
        } else {
          if (result.length() > 0) {
            result.append(" + ");
          }
        }
        if (degree == 0 || coefficient != 1) {
          int alphaPower = field.log(coefficient);
          if (alphaPower == 0) {
            result.append('1');
          } else if (alphaPower == 1) {
            result.append('a');
          } else {
            result.append("a^");
            result.append(alphaPower);
          }
        }
        if (degree != 0) {
          if (degree == 1) {
            result.append('x');
          } else {
            result.append("x^");
            result.append(degree);
          }
        }
      }
    }
    return result.toString();
  }

}
