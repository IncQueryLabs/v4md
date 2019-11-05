package com.incquerylabs.v4md;

import com.incquerylabs.v4md.expressions.BinaryVQLExpression;
import com.incquerylabs.v4md.internal.IProjectChangedListener;
import com.incquerylabs.v4md.transformations.IncrementalTransformationSynchronizer;
import com.nomagic.ci.persistence.IProject;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.ProjectUtilities;
import com.nomagic.magicdraw.core.modules.ModuleUsage;
import com.nomagic.magicdraw.core.project.ProjectEventListenerAdapter;
import com.nomagic.magicdraw.core.project.ProjectPartListener;
import com.nomagic.magicdraw.expressions.evaluation.ExpressionEvaluationConfigurator;

public class V4MDPlugin extends com.nomagic.magicdraw.plugins.Plugin {

	private final class V4MDProjectListener extends ProjectEventListenerAdapter implements ProjectPartListener {

		@Override
		public void projectPreClosed(Project project) {
			// There is no need to explicitly initialize adapters; the dispose handles uninitialized adapters correctly
			IncrementalTransformationSynchronizer.disposeSynchronizer(project);
			ViatraQueryAdapter.disposeAdapter(project);
		}
		
		@Override
		public void projectPartLoaded(Project project, IProject storage) {
			IProjectChangedListener.MANAGER.notifyProjectListeners(project);
		}

		@Override
		public void projectPartAttached(ModuleUsage usage) {
			Project project = ProjectUtilities.getProject(usage.getUsed());
			IProjectChangedListener.MANAGER.notifyProjectListeners(project);
		}

		@Override
		public void projectPartDetached(ModuleUsage usage) {
			Project project = ProjectUtilities.getProject(usage.getUsed());
			IProjectChangedListener.MANAGER.notifyProjectListeners(project);
			
		}

		@Override
		public void projectPartRemoved(IProject project) {
			Project modelProject = ProjectUtilities.getProject(project);
			IProjectChangedListener.MANAGER.notifyProjectListeners(modelProject);
		}
	}

	@Override
	public void init() {
		Application.getInstance().getProjectsManager().addProjectListener(new V4MDProjectListener());
		
		// Registers an expression evaluator for generated VIATRA queries
		ExpressionEvaluationConfigurator.getInstance().registerFactory(BinaryVQLExpression.LANGUAGE,
				BinaryVQLExpression::new);
	}
	
	@Override
	public boolean close() {
		return true;
	}
	
	@Override
	public boolean isSupported() {
		return true;
	}
}
