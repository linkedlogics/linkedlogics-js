package io.linkedlogics.js.script;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.script;
import static io.linkedlogics.LinkedLogicsBuilder.fromText;
import static io.linkedlogics.LinkedLogicsBuilder.fromFile;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.var;
import static io.linkedlogics.LinkedLogicsBuilder.verify;
import static io.linkedlogics.LinkedLogicsBuilder.when;
import static io.linkedlogics.js.script.ProcessTestHelper.waitUntil;
import static io.linkedlogics.LinkedLogicsBuilder.error;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.annotation.Input;
import io.linkedlogics.annotation.Logic;
import io.linkedlogics.context.Context;
import io.linkedlogics.context.Status;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.service.ContextService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.local.LocalServiceConfigurer;
import io.linkedlogics.js.service.JsServiceConfigurer;

public class ScriptProcess1Tests {

	private static ContextService contextService;

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.configure(new JsServiceConfigurer());
		LinkedLogics.registerLogic(ScriptProcess1Tests.class);
		LinkedLogics.registerProcess(ScriptProcess1Tests.class);
		LinkedLogics.launch();
		contextService = ServiceLocator.getInstance().getContextService();
	}

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_1", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v3");
		assertThat(ctx.getParams().get("text")).isEqualTo("3 items");
	}

	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3").build())
				.add(script(fromText("text = list.size() + ' items'")).returnAsMap().build())
				.build();
	}
	
	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_2", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v3");
		assertThat(ctx.getParams().get("text")).isEqualTo("3 items");
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3").build())
				.add(verify(when("false")).elseFailWithCode(-1).handle(error().usingLogic(script(fromText("list.size() + ' items'")).returnAs("text").build()).build()).build())
				.build();
	}
	
	@Test
	public void testScenario3() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_3", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("John Doe", "New York", "NY");
		assertThat(ctx.getParams().get("text")).isEqualTo("3 items");
	}

	public static ProcessDefinition scenario3() {
		String script = "obj = {\"name\": \"John Doe\", \"age\": 30, \"address\": {\"city\": \"New York\", \"state\": \"NY\"}}; \n"
				+ "obj";
		
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(script(fromText(script)).returnAsMap().build())
				.add(logic("INSERT").input("list", expr("list")).input("val", var("obj.name")).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", var("obj.address.city")).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", var("obj.address.state")).build())
				.add(script(fromText("list.size() + ' items'")).returnAs("text").build())
				.build();
	}
	
	@Test
	public void testScenario4() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_4", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("John Doe", "New York", "NY");
		assertThat(ctx.getParams().get("text")).isEqualTo("3 items");
	}

	public static ProcessDefinition scenario4() {
		String script = "obj = [\"John Doe\", \"New York\", \"NY\"]; \n"
				+ "obj";
		
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(script(fromText(script)).returnAs("names").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", var("names[0]")).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", var("names[1]")).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", var("names[2]")).build())
				.add(script(fromText("list.size() + ' items'")).returnAs("text").build())
				.build();
	}
	
	@Test
	public void testScenario5() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_5", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v12", "John");
		assertThat(ctx.getParams().get("text")).isEqualTo("3 items");
	}

	public static ProcessDefinition scenario5() {
		return createProcess("SIMPLE_SCENARIO_5", 0)
				.add(script(fromFile("scripts/script.js")).returnAsMap().build())
				.add(logic("INSERT").input("list", expr("list")).input("val", var("val1")).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", var("val2")).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", var("person.name")).build())
				.add(script(fromText("list.size() + ' items'")).returnAs("text").build())
				.build();
	}

	@Logic(id = "INSERT", version = 1)
	public static void insert(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.add(value);
	}

	@Logic(id = "INSERT", version = 0)
	public static void insertv2(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.add(value.toUpperCase());
	}

	@Logic(id = "REMOVE")
	public static void remove(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.remove(value);
	}

	@Logic(id = "PRINT")
	public static void print(@Input("list") List<String> list) {
		System.out.println("List = " + list);
	}

}
