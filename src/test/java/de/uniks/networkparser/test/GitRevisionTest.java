package de.uniks.networkparser.test;

import java.io.IOException;

import org.junit.Test;

import de.uniks.networkparser.ext.GitRevision;

public class GitRevisionTest {
	
	@Test
	public void testRevision() throws IOException {
//	, GitAPIException {
		GitRevision revision = new GitRevision();
		revision.withPath("D:\\Arbeit\\workspace\\de.sma.iguana.framework");
//		FileRepository repository = (FileRepository) revision.getRepository();
//		//Ref ref = var.getRef("e27ad43fb07e3944b3ee371728fc1a8fe5bb8aa0");
//		//Ref develop = var.getRef("976d8405a442e0acbbba81be5f252126635cf863");
//		ObjectId performance = repository.resolve("e27ad43fb07e3944b3ee371728fc1a8fe5bb8aa0");
		
//		
//		String 	 = "refs/heads/master"; // tag or branch
//		Git git = new Git(repository);
//		for (RevCommit commit : git.log().add(repository.resolve(treeName)).call()) {
//		    System.out.println(commit.getName());
//		}		
//		
//		SimpleList<String> allHistory = getAllHistory(performance);
//		ObjectId develop = var.resolve("976d8405a442e0acbbba81be5f252126635cf863");
//		// DEV 
//		System.out.println(var);
//	}
	
//	    Repository repo = new FileRepository("pathToRepo/.git");
//	    Git git = new Git(repository);
//	    RevWalk walk = new RevWalk(repository);
//
//        ObjectId develop = repository.resolve("976d8405a442e0acbbba81be5f252126635cf863");
//        Iterable<RevCommit> commitsDevelop = git.log().add(develop).call();
//	    SimpleList<String> developList=new SimpleList<String>(); 
//	    RevCommit targetCommit = commitsDevelop.iterator().next();
//	    addAllParent(walk, developList, repository, targetCommit);
//	    System.out.println(developList);
	    
	    
//	    RevCommit targetCommit = walk.parseCommit(repository.resolve(commit.getName()));
//	    developList.add(null)
//        for (RevCommit commit : commitsDevelop) {
//        	commit.getParents();
//        	RevCommit targetCommit = walk.parseCommit(repository.resolve(commit.getName()));
//        	developList.add(commit.getName());
//        	System.out.println(commit.getName());
//        }
//
	    
//	    
//	    Iterable<RevCommit> commits = git.log().add(performance).call();
//	    SimpleList<String> branchList=new SimpleList<String>(); 
//        for (RevCommit commit : commits) {
//        	RevCommit targetCommit = walk.parseCommit(repository.resolve(commit.getName()));
//        	if(developList.contains(targetCommit.getName()))
//        	{
//        		break;
//        	}
//        	branchList.add(commit.getName());
//        }
//        System.out.println(branchList);

//	    for (Ref branch : branches) {
//	        String branchName = branch.getName();
//
//	        System.out.println("Commits of branch: " + branch.getName());
//	        System.out.println("-------------------------------------");
//
//	        Iterable<RevCommit> commits = git.log().all().call();
//
//	        for (RevCommit commit : commits) {
//	            boolean foundInThisBranch = false;
//
//	            RevCommit targetCommit = walk.parseCommit(repository.resolve(
//	                    commit.getName()));
//	            for (Map.Entry<String, Ref> e : repository.getAllRefs().entrySet()) {
//	                if (e.getKey().startsWith(Constants.R_HEADS)) {
//	                    if (walk.isMergedInto(targetCommit, walk.parseCommit(
//	                            e.getValue().getObjectId()))) {
//	                        String foundInBranch = e.getValue().getName();
//	                        if (branchName.equals(foundInBranch)) {
//	                            foundInThisBranch = true;
//	                            break;
//	                        }
//	                    }
//	                }
//	            }
//
//	            if (foundInThisBranch) {
//	                System.out.println(commit.getName());
//	                System.out.println(commit.getAuthorIdent().getName());
//	                System.out.println(new java.util.Date(commit.getCommitTime() * 1000L));
//	                System.out.println(commit.getFullMessage());
//	            }
//	        }
//	    }
	}
	
//	
//	private void addAllParent(RevWalk walk, SimpleList<String> developList, FileRepository repository, RevCommit commit) throws RevisionSyntaxException, MissingObjectException, IncorrectObjectTypeException, AmbiguousObjectException, IOException {
//		commit = walk.parseCommit(repository.resolve(commit.getName()));
//		if(developList.add(commit.getName())) {
//			RevCommit[] parents = commit.getParents();
//			if(parents != null) {
//				for(RevCommit parent : parents) {
//					addAllParent(walk, developList, repository, parent);
//				}
//			}
//		}
//	}
//
//	public SimpleList<String> getAllHistory(ObjectId currentRepo)
//	{
//		SimpleList<String> ids = new SimpleList<String>();
//		ids.add(currentRepo.getName());
//		
//		 
//		return ids;
//		
//	}

}
