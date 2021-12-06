/**
 * This is free and unencumbered software released into the public domain.
 * 
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * 
 * For more information, please refer to <https://unlicense.org>
 */
package com.github.mfisch2011.gradle.plugins;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RevertCommand;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.ServiceUnavailableException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.TestDescriptor;
import org.gradle.api.tasks.testing.TestResult;

import groovy.lang.Closure;

/**
 * TODO:
 */
public class TestRevertCommitClosure extends Closure<Test> {

	/**
	 * TODO:
	 */
	private static final long serialVersionUID = 3413197525441594253L;

	public TestRevertCommitClosure(Object owner) {
		super(owner);
		System.out.printf("Owner: %s%n",owner.getClass());
	}

	/**
	 * TODO:
	 * @param description
	 * @param result
	 * @throws IOException 
	 * @throws GitAPIException 
	 * @throws WrongRepositoryStateException 
	 * @throws ConcurrentRefUpdateException 
	 * @throws UnmergedPathsException 
	 * @throws NoMessageException 
	 */
	public void doCall(TestDescriptor description,TestResult result) throws
	IOException, NoMessageException, UnmergedPathsException,
	ConcurrentRefUpdateException, WrongRepositoryStateException,
	GitAPIException {
		/*
		 * only the final summary has null parent, 
		 * ignore all except final summary test
		 */
		if(description.getParent()==null) {
			if(result.getFailedTestCount()>0)
				doRevert();
			else
				doCommit();
		}
	}
	
	/**
	 * TODO:
	 * @throws IOException 
	 * @throws GitAPIException 
	 * @throws WrongRepositoryStateException 
	 * @throws UnmergedPathsException 
	 * @throws ServiceUnavailableException 
	 * @throws NoMessageException 
	 * @throws NoHeadException 
	 * @throws ConcurrentRefUpdateException 
	 * @throws AbortedByHookException 
	 */
	protected void doCommit() throws IOException,
	AbortedByHookException, ConcurrentRefUpdateException,
	NoHeadException, NoMessageException, ServiceUnavailableException,
	UnmergedPathsException, WrongRepositoryStateException, GitAPIException {
		Git git = openRepository();
		CommitCommand commit = git.commit();
		commit.setAll(true);
		commit.setMessage(getCommitMessage());
		//TODO:additional configuration...
		commit.call();
	}
	
	/**
	 * TODO:
	 * @return
	 */
	protected String getCommitMessage() {
		return "TCR Commit"; //TODO:make configurable or more descriptive...
	}

	/**
	 * TODO:
	 * @throws IOException 
	 * @throws GitAPIException 
	 * @throws WrongRepositoryStateException 
	 * @throws ConcurrentRefUpdateException 
	 * @throws UnmergedPathsException 
	 * @throws NoMessageException 
	 */
	protected void doRevert() throws IOException,
	NoMessageException, UnmergedPathsException,
	ConcurrentRefUpdateException,
	WrongRepositoryStateException, GitAPIException {
		Git git = openRepository();
		RevertCommand revert = git.revert();
		//TODO:any additional configuration???
		revert.call();
	}
	
	

	/**
	 * TODO:
	 * @return
	 * @throws IOException 
	 */
	protected Git openRepository() throws IOException {
		return Git.open(getRootDir());
	}

	/**
	 * TODO:
	 * @return
	 */
	protected File getRootDir() {
		return getTest().getProject().getRootDir();
	}

	/**
	 * TODO:
	 * @return
	 */
	public Test getTest() {
		return (Test)getOwner();
	}
}
