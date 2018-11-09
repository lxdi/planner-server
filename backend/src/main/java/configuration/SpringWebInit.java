package configuration;

import model.WeeksGenerator;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Created by Alexander on 23.02.2018.
 */

public class SpringWebInit implements WebApplicationInitializer {
	@Override
	public void onStartup(ServletContext container) throws ServletException {
		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(SpringConfig.class);
		ctx.setServletContext(container);

		ServletRegistration.Dynamic servlet = container.addServlet("dispatcher", new DispatcherServlet(ctx));
		servlet.setAsyncSupported(true);
		servlet.setLoadOnStartup(1);
		servlet.addMapping("/");

		ctx.addApplicationListener(new ApplicationListener<ApplicationEvent>() {
			@Override
			public void onApplicationEvent(ApplicationEvent event) {
				if (event instanceof ContextRefreshedEvent) {
					Properties extprops = BeanFactoryAnnotationUtils.qualifiedBeanOfType(ctx.getBeanFactory(), Properties.class, "ext_props");
					//Properties extprops = (Properties) ctx.getBean("ext_props");
					if (extprops.getProperty("generate_weeks").toLowerCase().equals("true")) {
						generateWeeks(extprops.getProperty("gen_weeks_from"), extprops.getProperty("gen_weeks_to"),
								(WeeksGenerator) ctx.getBean("weeksGenerator"));
					}
				}
			}
		});
	}

	private void generateWeeks(String from, String to, WeeksGenerator generator){
		List<Integer> years = new ArrayList<>();
		for(int i = Integer.parseInt(from); i<=Integer.parseInt(to); i++){
			years.add(i);
		}
		generator.generate(years);
	}
}
