package dev.linkedlogics.js;

import java.util.List;

import dev.linkedlogics.js.service.JsEvaluatorService;
import dev.linkedlogics.service.LinkedLogicsService;
import dev.linkedlogics.service.ServiceProvider;

public class JsServices extends ServiceProvider {
	@Override
	public List<LinkedLogicsService> getEvaluatingServices() {
		return List.of(new JsEvaluatorService());
	}
}