package dev.linkedlogics.js.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.NativeMap;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.NativePromise;
import org.mozilla.javascript.NativeSet;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.linkedlogics.js.script.JsContextFactory;
import dev.linkedlogics.service.EvaluatorService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsEvaluatorService implements EvaluatorService {
	private ConcurrentHashMap<String, Script> cache;
	private JsContextFactory contextFactory;
	
	public JsEvaluatorService() {
		this.contextFactory = new JsContextFactory();
		this.cache = new ConcurrentHashMap<>();
	}
	
	@Override
	public Object evaluate(String expression, Map<String, Object> params) {
		try (Context context = contextFactory.enterContext()) {
			Scriptable scope = context.initStandardObjects();
			params.entrySet().forEach(e -> {
				ScriptableObject.putProperty(scope, e.getKey(), e.getValue());
			});
			if (!cache.containsKey(expression)) {
				cache.put(expression, context.compileString(expression, expression, 0, null));
			}
			
			return convert(cache.get(expression).exec(context, scope), context, scope);
		} 
	}

	@Override
	public Object evaluateScript(String expression, String id, Map<String, Object> params) {
		try (Context context = contextFactory.enterContext()) {
			Scriptable scope = context.initStandardObjects();
			params.entrySet().forEach(e -> {
				ScriptableObject.putProperty(scope, e.getKey(), Context.javaToJS(e.getValue(), scope));
			});
			
			if (!cache.containsKey(id)) {
				cache.put(id, context.compileString(expression, id, 0, null));
			}
			
			Object result = cache.get(id).exec(context, scope);
			Object[] variables = ScriptableObject.getPropertyIds(scope);

			if (variables.length == 0) {
				return convert(result, context, scope);
			} else {
				Map<String, Object> output = new HashMap<>();
				
				Arrays.stream(variables).forEach(key -> {
					Object value = ScriptableObject.getProperty(scope, (String) key);
					Object converted = convert(value, context, scope);
					
					if (converted != null) {
						output.put((String) key, converted);
					}
				});
				return output;
			}
		} 
	}
	
	private Object convert(Object value, Context context, Scriptable scope) {
		try {
			if (value instanceof NativeFunction 
					|| value instanceof NativePromise) {
				return null;
			}
			if (value instanceof Wrapper) {
				return ((Wrapper) value).unwrap();
			} else if (value instanceof Undefined) {
				return null;
			} else if (value instanceof NativeObject || value instanceof NativeMap) {
				String json = NativeJSON.stringify(context, scope, value, null, null).toString();
				return new ObjectMapper().readValue(json, Map.class);
			} else if (value instanceof NativeArray) {
				String json = NativeJSON.stringify(context, scope, value, null, null).toString();
				return new ObjectMapper().readValue(json, List.class);
			} else if (value instanceof NativeSet) {
				String json = NativeJSON.stringify(context, scope, value, null, null).toString();
				return new ObjectMapper().readValue(json, Set.class);
			}
		} catch (JsonProcessingException e) {
			log.error(e.getLocalizedMessage(), e);
		}
		
		return value;
	}
	
	
	public static void main(String[] args) {
//		String script = "function a() {return 1;}";
//		String script = "5 +6";
//		String script = "list.add('v2')\nperson.age = 26";
//		String script = "new Date()";
//		String script = "var a = 5; obj = {name: 'John', age: 30, address: {city: 'New York', state: 'NY'}}; \nobj";
//		String script = "({name: 'John'})";
		
		Person p = new Person();
		p.setName("John");
		p.setAge(18);
		
//		Map<String, Object> params = Map.of("list", new ArrayList<>() {{ add("v1");}}, "person", p, "msisdn", "994505001365");
		
		String script = " vLang = 'AZ'; E_NA[vLang];";
		
		Map<String, Object> params = new HashMap<>() {{
			put("E_NA", Map.of("AZ", "Hormetli Abunechi, teessuf ki, Sizin sorgunuz yerine yetirilmemishdir. Lutfen bir azdan yene cehd edin.",
					   "EN", "Dear Subscriber, your request has not been processed. Please try again later.",
					   "RU", "Uvajayemiy Abonent, k sojaleniyu Vash zapros ne vipolnen. Pojaluysta poprobuyte yesho raz po pozje."));
		}};
		
		
		Object o = new JsEvaluatorService().evaluate(script, params);
		System.out.println(o);
		System.out.println(o.getClass().getName());
		
//		Map<String, Object> map = (Map<String, Object>) o;
//		System.out.println(((Map<String, Object>)map.get("obj")).get("name").getClass().getName());
//		Map<String, Object> params = Map.of("person", o);
//		
//		Object r = new JsEvaluatorService().evaluate("person.address.city", params);
//		System.out.println(r.getClass());
//		System.out.println(r);
	}
	
	@Data
	public static class Person {
		private String name;
		private int age;
	}
}
