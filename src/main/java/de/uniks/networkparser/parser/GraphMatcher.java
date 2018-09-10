package de.uniks.networkparser.parser;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationSet;
import de.uniks.networkparser.graph.AssociationTypes;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.AttributeSet;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzSet;
import de.uniks.networkparser.graph.GraphEntity;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphModel;
import de.uniks.networkparser.graph.GraphSimpleSet;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.MethodSet;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.graph.ParameterSet;
import de.uniks.networkparser.list.EntityComparator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.parser.differ.AddCondition;
import de.uniks.networkparser.parser.differ.AttributeChangeCondition;
import de.uniks.networkparser.parser.differ.MemberDiffer;
import de.uniks.networkparser.parser.differ.RemoveCondition;

public class GraphMatcher extends GraphEntity {
	private SimpleList<Match> clazzMatches = new SimpleList<Match>();
	
	private SimpleList<Match> attributeMatches = new SimpleList<Match>();

	private SimpleList<Match> associationMatches = new SimpleList<Match>();

	private SimpleList<Match> methodMatches = new SimpleList<Match>();
	private SimpleKeyValueList<Match, Match> possibleLinks = new SimpleKeyValueList<Match, Match>();
	private GraphModel oldModel;
	private GraphModel newModel;
	private GraphModel metaModel;
	private GraphSimpleSet diffs = new GraphSimpleSet();
	
	public GraphMatcher(GraphModel oldModel, GraphModel newModel) {
		this.oldModel = oldModel;
		this.newModel = newModel;
		createMatches();
	}
	
	public GraphMatcher(GraphModel oldModel, GraphModel newModel, GraphModel metaModel) {
		this.oldModel = oldModel;
		this.newModel = newModel;
		this.metaModel = metaModel;
		createMatches();
	}
	public boolean createMatches() {
		matchClazzes();

		ClazzSet newMatches = new ClazzSet();

		if (metaModel != null) {
			for (Clazz metaClazz : metaModel.getClazzes()) {
				for (Match clazzMatch : getClazzMatches()) {
					Clazz clazz = (Clazz) clazzMatch.getMatch();
					if (newMatches.contains(clazz)) {
						continue;
					}
					if (matchClazzValues(metaClazz, clazz)) {
						clazzMatch.withMetdaMatch(metaClazz);
						newMatches.add(clazz);
					}
				}
			}
		}
		return true;
	}

//	private MemberDiffer clazzDiffer = new MemberDiffer(new ClazzAddCondition(), new ClazzChangeCondition(), new ClazzRemoveCondition());
	private MemberDiffer attributeDiffer = new MemberDiffer(new AddCondition(), new AttributeChangeCondition(), new RemoveCondition());
//	private MemberDiffer attributeDiffer = new MemberDiffer(new AddCondition());
//	private MemberDiffer associationDiffer = new MemberDiffer(new AssociationAddCondition(), new AssociationChangeCondition(), new AssociationRemoveCondition());
//	private MemberDiffer methodDiffer = new MemberDiffer(new AddCondition(), new MethodChangeCondition(), new RemoveCondition());


	public GraphSimpleSet getDiffs() {
//		clazzDiffer.diff(matches, getClazzMatches());
		attributeDiffer.diff(this, getAttributeMatches());
//		associationDiffer.diff(matches, getAssociationMatches());
//		methodDiffer.diff(matches, getMethodMatches());
		return diffs;
	}

	private List<Match> getAttributeMatches() {
		return attributeMatches;
	}

	public boolean matchClazzes() {
		ClazzSet oldClazzes = oldModel.getClazzes();
		ClazzSet newClazzes = newModel.getClazzes();
		
		prepareMatches(oldClazzes, false);
		prepareMatches(newClazzes, true);
		
		ClazzSet oldMatches = new ClazzSet();
		ClazzSet newMatches = new ClazzSet();
		
		for (Clazz oldClazz : oldClazzes) {
			for (Clazz newClazz : newClazzes) {
				if (newMatches.contains(newClazz)) {
					continue;
				}
				if (Double.compare(GraphUtil.compareName(oldClazz.getName(), newClazz.getName()), 3) == -1) {
					matchAttributes(oldClazz, newClazz);
					matchAssociations(oldClazz, newClazz);
					matchMethods(oldClazz, newClazz);
					Match oldMatch = this.getClazzMatch(oldClazz);
					Match newMatch = this.getClazzMatch(newClazz);
					linkMemberMatches(oldMatch, newMatch);
					oldMatches.add(oldClazz);
					newMatches.add(newClazz);
					break;
				}
			}
		}

		boolean matchEntries = true;
		
		SimpleKeyValueList<Double, SimpleList<Match>> potentMatches = preparePotentMatches(oldClazzes, newClazzes, oldMatches, newMatches);
		
		for (Entry<Double, SimpleList<Match>> potentEntry : potentMatches.entrySet()) {
			List<Match> potents = potentEntry.getValue();
			for (Match potentMatch : potents) {
				Clazz oldClazz = (Clazz) potentMatch.getMatch();
				if (oldMatches.contains(oldClazz)) {
					continue;
				}
				
				matchEntries = true;

				Clazz newClazz = (Clazz) potentMatch.getPotentMatch();
				if (newMatches.contains(newClazz)) {
					continue;
				}

				if (oldClazz.getAttributes().size() != newClazz.getAttributes().size()) {
					continue;
				}
				if (oldClazz.getAssociations().size() != newClazz.getAssociations().size()) {
					continue;
				}
				if (oldClazz.getMethods().size() != newClazz.getMethods().size()) {
					continue;
				}

				if (matchAttributes(oldClazz, newClazz) == false) {
					matchEntries = false;
				}
				if (matchEntries && matchAssociations(oldClazz, newClazz) == false) {
					matchEntries = false;
				}
				if (matchEntries && matchMethods(oldClazz, newClazz) == false) {
					matchEntries = false;
				}
				
				if (matchEntries) {
					linkMemberMatches(this.getClazzMatch(oldClazz), this.getClazzMatch(newClazz));
					oldMatches.add(oldClazz);
					newMatches.add(newClazz);
				} else {
					cleanLinks(oldClazz);
				}
			}
		}
		
		for (Entry<Match, Match> link : this.getPossibleLinks().entrySet()) {
			linkMemberMatches(link.getKey(), link.getValue());
		}
		return matchEntries;
	}
	
	private void prepareMatches(ClazzSet clazzes, boolean isFileMatch) {
		for (Clazz clazz : clazzes) {
			Match match = Match.createMatch(this, clazz, isFileMatch);
			match.withOwner(this);
			this.addClazzMatch(match);
			for (Attribute attribute : clazz.getAttributes()) {
				Match attributeMatch = Match.createMatch(this, attribute, isFileMatch);
				attributeMatch.withOwner(this);
				this.addAttributeMatch(attributeMatch);
			}
			for (Association association : clazz.getAssociations()) {
				if (GraphUtil.isAssociation(association) == false) {
					continue;
				}
				Match associationMatch = Match.createMatch(this, association, isFileMatch);
				associationMatch.withOwner(this);
				this.addAssociationMatch(associationMatch);
				if (association.isSelfAssoc()) {
					Match associationOtherMatch = Match.createMatch(this, association.getOther(), isFileMatch);
					associationOtherMatch.withOwner(this);
					this.addAssociationMatch(associationOtherMatch);
				}
			}
			for (Method method : clazz.getMethods()) {
				Match methodMatch = Match.createMatch(this, method, isFileMatch);
				methodMatch.withOwner(this);
				this.addMethodMatch(methodMatch);
			}
		}
	}

	private void cleanLinks(Clazz oldClazz) {
		Map<Match, Match> possibleLinks = this.getPossibleLinks();
		for (Attribute oldAttribute : oldClazz.getAttributes()) {
			possibleLinks.remove(this.getAttributeMatch(oldAttribute));
		}
		for (Association oldAssociation : oldClazz.getAssociations()) {
			possibleLinks.remove(this.getAssociationMatch(oldAssociation));
		}
		for (Method oldMethod : oldClazz.getMethods()) {
			possibleLinks.remove(this.getMethodMatch(oldMethod));
		}
	}

	public Map<Match, Match> getPossibleLinks() {
		return possibleLinks;
	}

	private void linkMemberMatches(Match oldMatch, Match newMatch) {
		oldMatch.withOtherMatch(newMatch);
		// COPY ALL TO NEW
//		newMatch.withMetaParent(oldMatch.getMetaParent());
//		newMatch.withParent(oldMatch.getParent());
//		newMatch.withSourceParent(oldMatch.getSourceParent());
//		if(newMatch.getSourceParent() != null) {
//			newMatch.withSourceMatch(true);
//		}
		
//		if(oldMatch.isMetaMatch()) {
//			newMatch.withMetaMatch(true);
//		}
//		if(oldMatch.isSourceMatch()) {
//			newMatch.withSourceMatch(true);
//		}
//		if(oldMatch.isMetaSourceMatch()) {
//			newMatch.withMetaSourceMatch(true);
//		}
//		remove(oldMatch);
	}
//	
//	public boolean remove(Match match) {
//		GraphMember parent = match.getParent();
//		if(parent instanceof Clazz) {
//			return clazzMatches.remove(match);
//		}
//		if(parent instanceof Attribute) {
//			return attributeMatches.remove(match);
//		}
//		if(parent instanceof Association) {
//			return associationMatches.remove(match);
//		}
//		if(parent instanceof Method) {
//			return methodMatches.remove(match);
//		}
//		return false;
//	}
//	
//	private void cleanLinks(Clazz oldClazz) {
//		Map<Match, Match> possibleLinks = this.getPossibleLinks();
//		for (Attribute oldAttribute : oldClazz.getAttributes()) {
//			possibleLinks.remove(this.getAttributeMatch(oldAttribute));
//		}
//		for (Association oldAssociation : oldClazz.getAssociations()) {
//			possibleLinks.remove(this.getAssociationMatch(oldAssociation));
//		}
//		for (Method oldMethod : oldClazz.getMethods()) {
//			possibleLinks.remove(this.getMethodMatch(oldMethod));
//		}
//	}
//	

//	
//	
	private boolean matchAttributes(Clazz oldClazz, Clazz newClazz) {
		AttributeSet oldAttributes = oldClazz.getAttributes();
		AttributeSet newAttributes = newClazz.getAttributes();
		
		AttributeSet oldMatches = new AttributeSet();
		AttributeSet newMatches = new AttributeSet();
		
		for (Attribute oldAttribute : oldAttributes) {
			for (Attribute newAttribute : newAttributes) {
				if (newMatches.contains(newAttribute)) {
					continue;
				}
				if (Double.compare(GraphUtil.compareName(oldAttribute.getName(), newAttribute.getName()), 0) == 0) {
					this.putPossibleLink(this.getAttributeMatch(oldAttribute), this.getAttributeMatch(newAttribute));
					oldMatches.add(oldAttribute);
					newMatches.add(newAttribute);
					break;
				}
			}
		}
		
		SimpleKeyValueList<Double, SimpleList<Match>> potentMatches = preparePotentMatches(oldAttributes, newAttributes, oldMatches, newMatches);
		
		SortedSet<Double> keys = new TreeSet<Double>(potentMatches.keySet());
		
		for (Double key : keys) {
			List<Match> potents = potentMatches.get(key);
			for (Match potentMatch : potents) {
				Attribute oldAttribute = (Attribute) potentMatch.getMatch();
				if (oldMatches.contains(oldAttribute)) {
					continue;
				}
				
				Attribute newAttribute = (Attribute) potentMatch.getPotentMatch();
				if (newMatches.contains(newAttribute)) {
					continue;
				}
				
				if (GraphUtil.compareType(oldAttribute.getType().getName(true), newAttribute.getType().getName(true)) == 0) {
					this.putPossibleLink(this.getAttributeMatch(oldAttribute), this.getAttributeMatch(newAttribute));
					oldMatches.add(oldAttribute);
					newMatches.add(newAttribute);
				}
			}	
		}
		
		return oldMatches.size() == oldAttributes.size() && newMatches.size() == newAttributes.size();
	}

	public void putPossibleLink(Match sourceMatch, Match otherMatch) {
		this.possibleLinks.put(sourceMatch, otherMatch);
	}

	private SimpleKeyValueList<Double, SimpleList<Match>> preparePotentMatches(
			SimpleSet<? extends GraphMember> oldMembers,
			SimpleSet<? extends GraphMember> newMembers,
			SimpleSet<? extends GraphMember> oldMatches,
			SimpleSet<? extends GraphMember> newMatches) {
		SimpleKeyValueList<Double, SimpleList<Match>> potentMatches = new SimpleKeyValueList<Double, SimpleList<Match>>().withComparator(EntityComparator.createComparator());

		CharacterBuffer oldBuffer = new CharacterBuffer();
		CharacterBuffer newBuffer = new CharacterBuffer();

		for (GraphMember oldMember : oldMembers) {
			if (oldMatches.contains(oldMember)) {
				continue;
			}
			for (GraphMember newMember : newMembers) {
				if (newMatches.contains(newMember)) {
					continue;
				}
				boolean executed = false;
				if (oldMember instanceof Association) {
					Association oldAssociation = (Association) oldMember;
					Association newAssociation = (Association) newMember;
					if (oldAssociation.getType().equals(AssociationTypes.EDGE)
							|| newAssociation.getType().equals(AssociationTypes.EDGE)) {
						oldBuffer.set(oldAssociation.getOther().getName());
						newBuffer.set(newAssociation.getOther().getName());
						executed = true;
					}
				}
				if (executed == false) {
					oldBuffer.set(oldMember.getName());
					newBuffer.set(newMember.getName());
				}
				Match potentMatch = Match.createPotentMatch(oldMember, newMember);
				potentMatch.withOwner(this);
				double distance = Math.abs(oldBuffer.equalsLevenshtein(newBuffer));
				if (potentMatches.containsKey(distance)) {
					potentMatches.get(distance).add(potentMatch);
				} else {
					SimpleList<Match> potents = new SimpleList<Match>();
					potents.add(potentMatch);
					potentMatches.add(distance, potents);
				}
			}
		}
		
		return potentMatches;
	}

	private boolean matchAssociations(Clazz oldClazz, Clazz newClazz) {
		AssociationSet oldAssociations = oldClazz.getAssociations();
		AssociationSet newAssociations = newClazz.getAssociations();
		
		AssociationSet oldMatches = new AssociationSet();
		AssociationSet newMatches = new AssociationSet();
		
		for (Association oldAssociation : oldAssociations) {
			if (GraphUtil.isAssociation(oldAssociation) == false) {
				oldMatches.add(oldAssociation);
			}
		}
		for (Association newAssociation : newAssociations) {
			if (GraphUtil.isAssociation(newAssociation) == false) {
				newMatches.add(newAssociation);
			}
		}

		for (Association oldAssociation : oldAssociations) {
			if (oldMatches.contains(oldAssociation)) {
				continue;
			}
			for (Association newAssociation : newAssociations) {
				if (newMatches.contains(newAssociation)) {
					continue;
				}
				if (checkAssociationNames(oldAssociation, newAssociation, 0, 0)) {
					if (oldAssociation.getClazz() == oldAssociation.getOtherClazz()
							&& newAssociation.getClazz() == newAssociation.getOtherClazz()) {
						this.putPossibleLink(this.getAssociationMatch(oldAssociation.getOther()), this.getAssociationMatch(newAssociation.getOther()));
					}
					this.putPossibleLink(this.getAssociationMatch(oldAssociation), this.getAssociationMatch(newAssociation));
					oldMatches.add(oldAssociation);
					newMatches.add(newAssociation);
					break;
				}
			}
		}

		SimpleKeyValueList<Double, SimpleList<Match>> potentMatches = preparePotentMatches(oldAssociations, newAssociations, oldMatches, newMatches);
		
		SortedSet<Double> keys = new TreeSet<Double>(potentMatches.keySet());
		
		for (Double key : keys) {
			List<Match> potents = potentMatches.get(key);
			for (Match potentMatch : potents) {
				Association oldAssociation = (Association) potentMatch.getMatch();
				if (oldMatches.contains(oldAssociation)) {
					continue;
				}

				Association newAssociation = (Association) potentMatch.getPotentMatch();
				if (newMatches.contains(newAssociation)) {
					continue;
				}
				
				if (oldClazz.getName().equals(newClazz.getName())
					|| checkAssociationNames(oldAssociation, newAssociation, 3, -1)) {
					if (oldAssociation.getClazz() == oldAssociation.getOtherClazz()
							&& newAssociation.getClazz() == newAssociation.getOtherClazz()) {
						this.putPossibleLink(this.getAssociationMatch(oldAssociation.getOther()), this.getAssociationMatch(newAssociation.getOther()));
					}
					this.putPossibleLink(this.getAssociationMatch(oldAssociation), this.getAssociationMatch(newAssociation));
					oldMatches.add(oldAssociation);
					newMatches.add(newAssociation);
				}
			}
		}
		
		return oldMatches.size() == oldAssociations.size() && newMatches.size() == newAssociations.size();
	}
	
	private boolean checkAssociationNames(Association oldAssociation, Association newAssociation, int distance, int equals) {
		if ((oldAssociation.getType() == AssociationTypes.EDGE && newAssociation.getType() != AssociationTypes.EDGE)) {
			if (Double.compare(GraphUtil.compareName(oldAssociation.getName(), newAssociation.getName()), distance) == equals) {
				return true;
			}
		}
		if ((oldAssociation.getOther().getType() == AssociationTypes.EDGE && newAssociation.getOther().getType() != AssociationTypes.EDGE)) {
			if (Double.compare(GraphUtil.compareName(oldAssociation.getOther().getName(), newAssociation.getOther().getName()), distance) == equals) {
				return true;
			}
		}
		return false;
	}

	public Match getAssociationMatch(GraphMember association) {
		for (Match associationMatch : associationMatches) {
			if (associationMatch.getMatch() == association) {
				return associationMatch;
			}
		}
		return null;
	}

	private boolean matchMethods(Clazz oldClazz, Clazz newClazz) {
		MethodSet oldMethods = oldClazz.getMethods();
		MethodSet newMethods = newClazz.getMethods();
		
		MethodSet oldMatches = new MethodSet();
		MethodSet newMatches = new MethodSet();
		
		for (Method oldMethod : oldMethods) {
			for (Method newMethod : newMethods) {
				if (newMatches.contains(newMethod)) {
					continue;
				}
				if (Double.compare(GraphUtil.compareName(oldMethod.getName(), newMethod.getName()), 0) == 0) {
					this.putPossibleLink(this.getMethodMatch(oldMethod), this.getMethodMatch(newMethod));
					oldMatches.add(oldMethod);
					newMatches.add(newMethod);
					break;
				}
			}
		}
		
		SimpleKeyValueList<Double, SimpleList<Match>> potentMatches = preparePotentMatches(oldMethods, newMethods, oldMatches, newMatches);
		
		SortedSet<Double> keys = new TreeSet<Double>(potentMatches.keySet());
		
		for (Double key : keys) {
			List<Match> potents = potentMatches.get(key);
			for (Match potentMatch : potents) {
				Method oldMethod = (Method) potentMatch.getMatch();
				if (oldMatches.contains(oldMethod)) {
					continue;
				}

				Method newMethod = (Method) potentMatch.getPotentMatch();
				if (newMatches.contains(newMethod)) {
					continue;
				}

				if (GraphUtil.compareType(oldMethod.getReturnType().getName(true), newMethod.getReturnType().getName(true)) == 0) {
					this.putPossibleLink(this.getMethodMatch(oldMethod), this.getMethodMatch(newMethod));
					oldMatches.add(oldMethod);
					newMatches.add(newMethod);
				}
			}
		}
		
		return oldMatches.size() == oldMethods.size() && newMatches.size() == newMethods.size();
	}
	
	public Match getAttributeMatch(GraphMember attribute) {
		for (Match attributeMatch : attributeMatches) {
			if (attributeMatch.getMatch()  == attribute) {
				return attributeMatch;
			}
		}
		return null;
	}
	
	public Match getMethodMatch(GraphMember method) {
		for (Match methodMatch : methodMatches) {
			if (methodMatch.getMatch() == method) {
				return methodMatch;
			}
		}
		return null;
	}
	
	public Match getClazzMatch(GraphMember clazz) {
		for (Match clazzMatch : clazzMatches) {
			if (clazzMatch.getMatch() == clazz) {
				return clazzMatch;
			}
		}
		return null;
	}
	
	
	private boolean matchClazzValues(Clazz oldClazz, Clazz newClazz) {
		if (oldClazz.getName().equals(newClazz.getName()) == false) {
			return false;
		}
		
		AttributeSet oldAttributes = oldClazz.getAttributes();
		AttributeSet newAttributes = newClazz.getAttributes();
		AttributeSet metaMatchedAttributes = new AttributeSet();
		AttributeSet newMatchedAttributes = new AttributeSet();
		
		for (Attribute oldAttribute : oldAttributes) {
			for (Attribute newAttribute : newAttributes) {
				if (newMatchedAttributes.contains(newAttribute)) {
					continue;
				}
				if (newMatchedAttributes.contains(newAttribute) == false) {
					if (matchAttributeValues(oldAttribute, newAttribute)) {
						metaMatchedAttributes.add(oldAttribute);
						newMatchedAttributes.add(newAttribute);
					}
				}
			}
		}
		
		AssociationSet oldAssociations = oldClazz.getAssociations();
		AssociationSet newAssociations = newClazz.getAssociations();
		AssociationSet metaMatchedAssociations = new AssociationSet();
		AssociationSet newMatchedAssociations = new AssociationSet();
		
		for (Association oldAssociation : oldAssociations) {
			if(GraphUtil.isAssociation(oldAssociation) == false) {
				metaMatchedAssociations.add(oldAssociation);
			}
		}
		for (Association newAssociation : newAssociations) {
			if(GraphUtil.isAssociation(newAssociation) == false) {
				newMatchedAssociations.add(newAssociation);
			}
		}
		
		for (Association oldAssociation : oldAssociations) {
			if (metaMatchedAssociations.contains(oldAssociation)) {
				continue;
			}
			for (Association newAssociation : newAssociations) {
				if (newMatchedAssociations.contains(newAssociation)) {
					continue;
				}
				if (newMatchedAssociations.contains(newAssociation) == false) {
					if (matchAssociationValues(oldAssociation, newAssociation)) {
						metaMatchedAssociations.add(oldAssociation);
						newMatchedAssociations.add(newAssociation);
					
						if (oldAssociation.getClazz() == oldAssociation.getOtherClazz()
								&& newAssociation.getClazz() == newAssociation.getOtherClazz()) {
							matchAssociationValues(oldAssociation.getOther(), newAssociation.getOther());
						}
					}
				}
			}
		}
		
		MethodSet oldMethods = oldClazz.getMethods();
		MethodSet newMethods = newClazz.getMethods();
		MethodSet metaMatchedMethods = new MethodSet();
		MethodSet newMatchedMethods = new MethodSet();
		
		for (Method oldMethod : oldMethods) {
			for (Method newMethod : newMethods) {
				if (newMatchedMethods.contains(newMethod)) {
					continue;
				}
				if (newMatchedMethods.contains(newMethod) == false) {
					if (matchMethodValues(oldMethod, newMethod)) {
						metaMatchedMethods.add(oldMethod);
						newMatchedMethods.add(newMethod);
					}
				}
			}
		}
		
		ClazzSet oldSuperClazzes = oldClazz.getSuperClazzes(false);
		oldSuperClazzes.addAll(oldClazz.getInterfaces(false));
		ClazzSet newSuperClazzes = newClazz.getSuperClazzes(false);
		newSuperClazzes.addAll(newClazz.getInterfaces(false));
		ClazzSet metaMatchedSuperClazzes = new ClazzSet();
		ClazzSet newMatchedSuperClazzes = new ClazzSet();
		
		for (Clazz oldSuperClazz : oldSuperClazzes) {
			for (Clazz newSuperClazz : newSuperClazzes) {
				if (newMatchedSuperClazzes.contains(newSuperClazz)) {
					continue;
				}
				if (oldSuperClazz.getName().equals(newSuperClazz.getName())) {
					metaMatchedSuperClazzes.add(oldSuperClazz);
					newMatchedSuperClazzes.add(newSuperClazz);
				}
			}
		}
		
		return oldAttributes.size() == metaMatchedAttributes.size()
				&& newAttributes.size() == newMatchedAttributes.size()
				&& oldAssociations.size() == metaMatchedAssociations.size()
				&& newAssociations.size() == newMatchedAssociations.size()
				&& oldMethods.size() == metaMatchedMethods.size()
				&& newMethods.size() == newMatchedMethods.size()
				&& oldSuperClazzes.size() == metaMatchedSuperClazzes.size()
				&& newSuperClazzes.size() == newMatchedSuperClazzes.size()
				&& oldClazz.getType().equals(newClazz.getType())
				&& oldClazz.getModifier().toString().equals(newClazz.getModifier().toString());
	}

	private boolean matchAttributeValues(Attribute oldAttribute, Attribute newAttribute) {
		if (oldAttribute.getName().equals(newAttribute.getName()) == false) {
			return false;
		}
		if (oldAttribute.getType().equals(newAttribute.getType()) == false) {
			return false;
		}
		if (oldAttribute.getModifier().toString().equals(newAttribute.getModifier().toString()) == false) {
			return false;
		}
		
//FIXME		if (matchData != null) {
//			matchData.getAttributeMatch(newAttribute)
//			.withMetaMatch(true)
//			.withMetaParent(oldAttribute);
//		}
		
		return true;
	}
	
	private boolean matchAssociationValues(Association oldAssociation, Association newAssociation) {
		if (oldAssociation.getName().equals(newAssociation.getName()) == false) {
			return false;
		}
		if (oldAssociation.getType().equals(newAssociation.getType()) == false) {
			return false;
		}
		if (oldAssociation.getCardinality().equals(newAssociation.getCardinality()) == false) {
			return false;
		}
		if (oldAssociation.getModifier() != null && newAssociation.getModifier() != null) {
			if (oldAssociation.getModifier().toString().equals(newAssociation.getModifier().toString()) == false) {
				return false;
			}
		} else if (oldAssociation.getModifier() != null || newAssociation.getModifier() != null) {
			return false;
		}
		if (oldAssociation.getOther().getName().equals(newAssociation.getOther().getName()) == false) {
			return false;
		}
		if (oldAssociation.getOther().getType().equals(newAssociation.getOther().getType()) == false) {
			return false;
		}
		if (oldAssociation.getOther().getCardinality().equals(newAssociation.getOther().getCardinality()) == false) {
			return false;
		}
		if (oldAssociation.getOther().getModifier() != null && newAssociation.getModifier() != null) {
			if (oldAssociation.getOther().getModifier().toString().equals(newAssociation.getOther().getModifier().toString()) == false) {
				return false;
			}
		} else if (oldAssociation.getOther().getModifier() != null || newAssociation.getOther().getModifier() != null) {
			return false;
		}
		if (oldAssociation.getOtherClazz().getName().equals(newAssociation.getOtherClazz().getName()) == false) {
			return false;
		}
		
//FIXME		if (matchData != null) {
//			matchData.getAssociationMatch(newAssociation)
//			.withMetaMatch(true)
//			.withMetaParent(oldAssociation);
//		}

		return true;
	}
	
	private boolean matchMethodValues(Method oldMethod, Method newMethod) {
		if (oldMethod.getName().equals(newMethod.getName()) == false) {
			return false;
		}
		if (oldMethod.getModifier().toString().equals(newMethod.getModifier().toString()) == false) {
			return false;
		}
		if (oldMethod.getReturnType().equals(newMethod.getReturnType()) == false) {
			return false;
		}
		ParameterSet oldParameters = oldMethod.getParameters();
		ParameterSet newParameters = newMethod.getParameters();
		if (oldParameters.size() != newParameters.size()) {
			return false;
		}
		boolean found = false;
		for (Parameter oldParameter : oldParameters) {
			found = false;
			for (Parameter newParameter : newParameters) {
				if (oldParameter.getName() != null
						&& newParameter.getName() != null
						&& oldParameter.getName().equals(newParameter.getName()) == false) {
					continue;
				}
				if (oldParameter.getType().equals(newParameter.getType()) == false) {
					continue;
				}
				if (oldParameter.getModifier() == null) {
					if (newParameter.getModifier() != null) {
						continue;
					}
				} else if (oldParameter.getModifier().toString().equals(newParameter.getModifier().toString()) == false) {
					continue;
				}
				
				found = true;
				break;
			}
			if (found == false) {
				return false;
			}
		}
		if (oldMethod.getBody() == null) {
			if(newMethod.getBody() != null) {
				return false;
			}
		} else if(oldMethod.getBody().equals(newMethod.getBody()) == false) {
//		if (oldMethod.getBody() == newMethod.getBody() && oldMethod.getBody().equals(newMethod.getBody()) == false) {
			return false;
		}
		
//FIXME		if (matchData != null) {
//			matchData.getMethodMatch(newMethod)
//			.withMetaMatch(true)
//			.withMetaParent(oldMethod);
//		}
		
		return true;
	}

	public void addClazzMatch(Match clazzMatch) {
		this.clazzMatches.add(clazzMatch);
	}
	public void addAttributeMatch(Match attributeMatch) {
		this.attributeMatches.add(attributeMatch);
	}
	public void addAssociationMatch(Match associationMatch) {
		this.associationMatches.add(associationMatch);
	}
	public void addMethodMatch(Match methodMatch) {
		this.methodMatches.add(methodMatch);
	}
	public List<Match> getClazzMatches() {
		return clazzMatches;
	}

	public GraphModel getOldModel() {
		return oldModel;
	}
	public GraphModel getNewModel() {
		return newModel;
	}

	public boolean addDiff(Match diff) {
		return this.diffs.add(diff);
	}

	public Object getMetaModel() {
		return metaModel;
	}

	public boolean generate() {
		createMatches();
//		GraphSimpleSet diffs = getDiffs();
//		GraphConverter converter = new GraphConverter();
//		TemplateResultFragment fragment = converter.convertToAdvanced(TemplateResultFragment.create(diffs, true, true));
//		CharacterBuffer value = fragment.getValue();
		return true;
	}
}
