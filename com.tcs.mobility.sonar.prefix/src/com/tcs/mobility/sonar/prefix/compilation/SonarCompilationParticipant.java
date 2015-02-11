package com.tcs.mobility.sonar.prefix.compilation;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;

public class SonarCompilationParticipant extends CompilationParticipant {

	public SonarCompilationParticipant() {
		System.out.println("DONE");
	}

	@Override
	public boolean isActive(IJavaProject project) {
		return true;
	}

	@Override
	public void buildFinished(IJavaProject project) {
		System.out.println("Build finished...");
	}

	@Override
	public void buildStarting(BuildContext[] files, boolean isBatch) {
		System.out.println("Build Starting...");
	}

	@Override
	public void cleanStarting(IJavaProject project) {
		System.out.println("Clean starting...");
	}

	@Override
	public void processAnnotations(BuildContext[] files) {
		System.out.println("processing annotations...");
	}

	@Override
	public void reconcile(ReconcileContext context) {
		System.out.println("Reconciling...");
	}
	
	
}
