package de.uniks.networkparser.test;

import java.io.IOException;

import de.uniks.networkparser.ext.GitRevision;

public class GitTest {

	public static void main(String[] args) throws IOException {
		System.out.println(new GitRevision().execute());
	}
}
