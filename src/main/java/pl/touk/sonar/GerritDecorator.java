package pl.touk.sonar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.*;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;

//http://sonarqube.15.x6.nabble.com/sonar-dev-Decorator-executed-a-lot-of-times-td5011536.html
@InstantiationStrategy(InstantiationStrategy.PER_BATCH)
public class GerritDecorator implements Decorator {
    private final static Logger LOG = LoggerFactory.getLogger(GerritDecorator.class);
    private Settings settings;

    public GerritDecorator(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void decorate(Resource resource, DecoratorContext context) {
        LOG.info("Decorate on resource {} with this {}", resource, this);
        LOG.info("Has violations: {}", context.getViolations());
        if (ResourceUtils.isRootProject(resource)) {
            decorateProject((Project)resource, context);
        }
    }

    protected void decorateProject(Project project, DecoratorContext context) {
        Review review = new Review(project, context);
        review.setGerritHost(settings.getString(PropertyKey.GERRIT_HOST));
        review.setGerritHttpPort(settings.getInt(PropertyKey.GERRIT_HTTP_PORT));
        review.setGerritHttpUsername(settings.getString(PropertyKey.GERRIT_HTTP_USERNAME));
        review.setGerritHttpPassword(settings.getString(PropertyKey.GERRIT_HTTP_PASSWORD));
        review.setGerritProjectName(settings.getString(PropertyKey.GERRIT_PROJECT));
        review.setGerritChangeId(settings.getString(PropertyKey.GERRIT_CHANGE_ID));
        review.setGerritRevisionId(settings.getString(PropertyKey.GERRIT_REVISION_ID));
        new ProjectProcessor(review).process();

    }

    @DependsUpon
    public String dependsOnViolations() {
        return DecoratorBarriers.END_OF_VIOLATIONS_GENERATION;
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return true;
    }

    private boolean shouldDecorateResource(final Resource resource) {
        return ResourceUtils.isRootProject(resource);
    }
}
