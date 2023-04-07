package dev.linkedlogics.js.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import dev.linkedlogics.js.script.JsContextFactory;
import dev.linkedlogics.service.EvaluatorService;

public class JsEvaluatorService implements EvaluatorService {
	private ConcurrentHashMap<String, Script> cache;
	private JsContextFactory contextFactory;
	
	public JsEvaluatorService() {
		this.contextFactory = new JsContextFactory();
		this.cache = new ConcurrentHashMap<>();
	}

	@Override
	public Object evaluate(String expression, String id, Map<String, Object> params) {
		try (Context context = contextFactory.enterContext()) {
			Scriptable scope = context.initStandardObjects();
			params.entrySet().forEach(e -> {
				ScriptableObject.putProperty(scope, e.getKey(), e.getValue());
			});
			if (!cache.containsKey(id)) {
				cache.put(id, context.compileString(expression, id, 0, null));
			}
			
			return cache.get(id).exec(context, scope);
		} 
	}
}
