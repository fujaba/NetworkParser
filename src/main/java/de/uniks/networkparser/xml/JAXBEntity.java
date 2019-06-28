package de.uniks.networkparser.xml;

import de.uniks.networkparser.SendableItem;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Annotation;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphMember;

public class JAXBEntity extends XSDEntity {
	private Clazz superClazz = new Clazz(SendableItem.class).withExternal(true);

	public JAXBEntity withSuperClazz(Clazz clazz) {
		this.superClazz = clazz;
		return this;
	}

	public JAXBEntity withContainerClazz(Clazz clazz) {
		this.container = clazz;
		return this;
	}

	@Override
	protected boolean callBack(GraphMember member, boolean value, String... params) {
		if (member instanceof Clazz) {
			Clazz clazz = (Clazz) member;
			if (params != null && params.length > 0) {
				Annotation nextAnnotatation = null;
				if (value) {
					/* Its is a Orderkey */
					CharacterBuffer sb = new CharacterBuffer();
					sb.with('{');
					for (int i = 0; i < params.length; i++) {
						if (i > 0) {
							sb.with(", \"" + params[i] + "\"");
						} else {
							sb.with("\"" + params[i] + "\"");
						}
					}
					sb.with('}');
					nextAnnotatation = Annotation.create("XmlType", "propOrder", sb.toString())
							.withImport("javax.xml.bind.annotation.XmlType");
				} else {
					/* NEW XMLROOTELement */
					nextAnnotatation = Annotation.create("XmlRootElement", "name", params[0])
							.withImport("javax.xml.bind.annotation.XmlRootElement");
					clazz.withSuperClazz(superClazz);
				}
				Annotation annotation = clazz.getAnnotation();
				if (annotation != null) {
					annotation.withNext(nextAnnotatation);
				} else {
					clazz.with(nextAnnotatation);
				}
				return true;
			}
			if (value) {
				clazz.with(Annotation.create("XmlRootElement").withImport("javax.xml.bind.annotation.XmlRootElement"));
			}
			clazz.withSuperClazz(superClazz);
		} else if (member instanceof Attribute) {
			Attribute attribute = (Attribute) member;
			if (params != null && params.length > 1) {
				Annotation anno = Annotation.create("XmlElementWrapper", "name", params[0])
						.withImport("javax.xml.bind.annotation.XmlElementWrapper").withScope("getter");
				anno.withNext(Annotation.create("XmlElement", "name", params[1])
						.withImport("javax.xml.bind.annotation.XmlElement").withScope("getter"));
				attribute.with(anno);
			} else if (value) {
				Annotation xmltransient = Annotation.create("XmlTransient").withScope("getter")
						.withImport("javax.xml.bind.annotation.XmlTransient");
				Annotation anno = Annotation.create("XmlElement", "name", attribute.getName())
						.withImport("javax.xml.bind.annotation.XmlElement");
				xmltransient.withNext(anno);
				attribute.with(xmltransient);
			} else {
				attribute.with(Annotation.create("XmlAttribute", "name", attribute.getName())
						.withImport("javax.xml.bind.annotation.XmlAttribute").withScope("getter"));
			}
		} else if (member instanceof Association) {
			Association assoc = (Association) member;
			Association other = assoc.getOther();
			other.with(Annotation.create("XmlTransient").withScope("getter")
					.withImport("javax.xml.bind.annotation.XmlTransient"));
			assoc.with(Annotation.create("XmlElement", "name", other.getName())
					.withImport("javax.xml.bind.annotation.XmlElement"));
		}
		return super.callBack(member, value);
	}

	@Override
	public JAXBEntity getNewList(boolean keyValue) {
		return new JAXBEntity();
	}
}
