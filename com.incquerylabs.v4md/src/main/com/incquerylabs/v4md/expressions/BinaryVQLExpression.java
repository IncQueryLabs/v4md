package com.incquerylabs.v4md.expressions;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;

import com.incquerylabs.v4md.ViatraQueryAdapter;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.expressions.ParameterizedExpression;
import com.nomagic.magicdraw.expressions.ValueContext;
import com.nomagic.magicdraw.expressions.evaluation.ExpressionContext;
import com.nomagic.magicdraw.expressions.evaluation.ParameterizedExpressionDescriptor;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

/**
 * Provides a binary VQL expression script evaluator for MagicDraw, this script
 * consists of a fully qualified name of an instanceof of
 * {@link BinaryQueryResultProvider} that is resposible of calculating the match
 * results over a given query engine.
 * </p>
 *
 * <b>Note</b>: The provided {@link BinaryQueryResultProvider} instance must
 * have a parameterless constructor.
 * </p>
 * 
 * When using a binary VQL expression, a single parameter «sourceParameter» is
 * to be provided with the type of Element. This parameter will be forwarded to
 * the result provider instance.
 *
 */
public class BinaryVQLExpression implements ParameterizedExpression {

	public static final String LANGUAGE = "Binary VQL";

	protected Project project;
	protected String className;
	private BinaryQueryResultProvider provider;

	public BinaryVQLExpression(ParameterizedExpressionDescriptor descriptor, ExpressionContext context)
			throws Exception {
		this.project = context.getProject();
		this.className = descriptor.getExpression();

		Constructor<?> constructor = Class.forName(className).getConstructor();
		constructor.setAccessible(true);

		Object instance = constructor.newInstance();
		provider = instance instanceof BinaryQueryResultProvider ? (BinaryQueryResultProvider) instance : null;
	}

	public Object getValue(Element sourceParameter, ValueContext context) throws Exception {
		ViatraQueryEngine engine = ViatraQueryAdapter.getAdapter(project).get().getEngine();

		Set<Element> returnSet = new HashSet<Element>();
		if (provider != null) {
			provider.getResults(engine, sourceParameter).forEach(returnSet::add);
		}
		return returnSet;
	}

	@Override
	public Class<?> getResultType() {
		return provider != null ? null : provider.getResultType();
	}

}