/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.uniks.networkparser.bytes.qr;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Implements Reed-Solomon enbcoding, as the name implies.
 * </p>
 *
 * @author Sean Owen
 * @author William Rucklidge
 */
public final class ReedSolomon {

	private final GenericGF field;
	private final List<GenericGFPoly> cachedGenerators;

	public ReedSolomon(GenericGF field) {
		this.field = field;
		this.cachedGenerators = new ArrayList<GenericGFPoly>();
		cachedGenerators.add(new GenericGFPoly(field, new int[] { 1 }));
	}

	private GenericGFPoly buildGenerator(int degree) {
		if (degree >= cachedGenerators.size()) {
			GenericGFPoly lastGenerator = cachedGenerators.get(cachedGenerators.size() - 1);
			for (int d = cachedGenerators.size(); d <= degree; d++) {
				GenericGFPoly nextGenerator = lastGenerator.multiply(
						new GenericGFPoly(field, new int[] { 1, field.exp(d - 1 + field.getGeneratorBase()) }));
				cachedGenerators.add(nextGenerator);
				lastGenerator = nextGenerator;
			}
		}
		return cachedGenerators.get(degree);
	}

	public boolean encode(int[] toEncode, int ecBytes) {
		if (ecBytes == 0) {
			return false;
		}
		int dataBytes = toEncode.length - ecBytes;
		if (dataBytes <= 0) {
			return false;
		}
		GenericGFPoly generator = buildGenerator(ecBytes);
		int[] infoCoefficients = new int[dataBytes];
		System.arraycopy(toEncode, 0, infoCoefficients, 0, dataBytes);
		GenericGFPoly info = new GenericGFPoly(field, infoCoefficients);
		info = info.multiplyByMonomial(ecBytes, 1);
		GenericGFPoly remainder = info.divide(generator)[1];
		int[] coefficients = remainder.getCoefficients();
		int numZeroCoefficients = ecBytes - coefficients.length;
		for (int i = 0; i < numZeroCoefficients; i++) {
			toEncode[dataBytes + i] = 0;
		}
		System.arraycopy(coefficients, 0, toEncode, dataBytes + numZeroCoefficients, coefficients.length);
		return true;
	}

	/**
	 * <p>
	 * Decodes given set of received codewords, which include both data and
	 * error-correction codewords. Really, this means it uses Reed-Solomon to detect
	 * and correct errors, in-place, in the input.
	 * </p>
	 *
	 * @param received data and error-correction codewords
	 * @param twoS     number of error-correction codewords available
	 */
	public boolean decode(int[] received, int twoS) {
		GenericGFPoly poly = new GenericGFPoly(field, received);
		int[] syndromeCoefficients = new int[twoS];
		boolean noError = true;
		for (int i = 0; i < twoS; i++) {
			int eval = poly.evaluateAt(field.exp(i + field.getGeneratorBase()));
			syndromeCoefficients[syndromeCoefficients.length - 1 - i] = eval;
			if (eval != 0) {
				noError = false;
			}
		}
		if (noError) {
			return false;
		}
		GenericGFPoly syndrome = new GenericGFPoly(field, syndromeCoefficients);
		GenericGFPoly[] sigmaOmega = runEuclideanAlgorithm(field.buildMonomial(twoS, 1), syndrome, twoS);
		GenericGFPoly sigma = sigmaOmega[0];
		GenericGFPoly omega = sigmaOmega[1];
		int[] errorLocations = findErrorLocations(sigma);
		int[] errorMagnitudes = findErrorMagnitudes(omega, errorLocations);
		for (int i = 0; i < errorLocations.length; i++) {
			int position = received.length - 1 - field.log(errorLocations[i]);
			if (position < 0) {
				return false;
			}
			received[position] = GenericGF.addOrSubtract(received[position], errorMagnitudes[i]);
		}
		return true;
	}

	private GenericGFPoly[] runEuclideanAlgorithm(GenericGFPoly a, GenericGFPoly b, int R) {
		// Assume a's degree is >= b's
		if (a.getDegree() < b.getDegree()) {
			GenericGFPoly temp = a;
			a = b;
			b = temp;
		}

		GenericGFPoly rLast = a;
		GenericGFPoly r = b;
		GenericGFPoly tLast = field.getZero();
		GenericGFPoly t = field.getOne();

		// Run Euclidean algorithm until r's degree is less than R/2
		while (r.getDegree() >= R / 2) {
			GenericGFPoly rLastLast = rLast;
			GenericGFPoly tLastLast = tLast;
			rLast = r;
			tLast = t;

			// Divide rLastLast by rLast, with quotient in q and remainder in r
			if (rLast.isZero()) {
				// Oops, Euclidean algorithm already terminated?
				return null;
			}
			r = rLastLast;
			GenericGFPoly q = field.getZero();
			int denominatorLeadingTerm = rLast.getCoefficient(rLast.getDegree());
			int dltInverse = field.inverse(denominatorLeadingTerm);
			while (r.getDegree() >= rLast.getDegree() && !r.isZero()) {
				int degreeDiff = r.getDegree() - rLast.getDegree();
				int scale = field.multiply(r.getCoefficient(r.getDegree()), dltInverse);
				q = q.addOrSubtract(field.buildMonomial(degreeDiff, scale));
				r = r.addOrSubtract(rLast.multiplyByMonomial(degreeDiff, scale));
			}

			t = q.multiply(tLast).addOrSubtract(tLastLast);

			if (r.getDegree() >= rLast.getDegree()) {
				return null;
			}
		}

		int sigmaTildeAtZero = t.getCoefficient(0);
		if (sigmaTildeAtZero == 0) {
			return null;
		}

		int inverse = field.inverse(sigmaTildeAtZero);
		GenericGFPoly sigma = t.multiply(inverse);
		GenericGFPoly omega = r.multiply(inverse);
		return new GenericGFPoly[] { sigma, omega };
	}

	private int[] findErrorLocations(GenericGFPoly errorLocator) {
		// This is a direct application of Chien's search
		int numErrors = errorLocator.getDegree();
		if (numErrors == 1) { // shortcut
			return new int[] { errorLocator.getCoefficient(1) };
		}
		int[] result = new int[numErrors];
		int e = 0;
		for (int i = 1; i < field.getSize() && e < numErrors; i++) {
			if (errorLocator.evaluateAt(i) == 0) {
				result[e] = field.inverse(i);
				e++;
			}
		}
		if (e != numErrors) {
			return null;
		}
		return result;
	}

	private int[] findErrorMagnitudes(GenericGFPoly errorEvaluator, int[] errorLocations) {
		// This is directly applying Forney's Formula
		int s = errorLocations.length;
		int[] result = new int[s];
		for (int i = 0; i < s; i++) {
			int xiInverse = field.inverse(errorLocations[i]);
			int denominator = 1;
			for (int j = 0; j < s; j++) {
				if (i != j) {
					// denominator = field.multiply(denominator,
					// GenericGF.addOrSubtract(1,
					// field.multiply(errorLocations[j], xiInverse)));
					// Above should work but fails on some Apple and Linux JDKs
					// due to a Hotspot bug.
					// Below is a funny-looking workaround from Steven Parkes
					int term = field.multiply(errorLocations[j], xiInverse);
					int termPlus1 = (term & 0x1) == 0 ? term | 1 : term & ~1;
					denominator = field.multiply(denominator, termPlus1);
				}
			}
			result[i] = field.multiply(errorEvaluator.evaluateAt(xiInverse), field.inverse(denominator));
			if (field.getGeneratorBase() != 0) {
				result[i] = field.multiply(result[i], xiInverse);
			}
		}
		return result;
	}
}
