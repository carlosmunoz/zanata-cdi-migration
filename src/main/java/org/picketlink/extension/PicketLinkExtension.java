package org.picketlink.extension;

import lombok.extern.slf4j.Slf4j;
import org.picketlink.Identity;
import org.picketlink.config.SecurityConfiguration;
import org.picketlink.config.SecurityConfigurationBuilder;
import org.picketlink.event.SecurityConfigurationEvent;
import org.picketlink.internal.IdentityBeanDefinition;
import org.zanata.security.ZanataIdentityBeanDefinition;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;

import static org.picketlink.log.BaseLog.ROOT_LOGGER;

/**
 * Copy from PicketLink.
 */
@Slf4j
public class PicketLinkExtension implements Extension {

    private ZanataIdentityBeanDefinition identityBeanDefinition;
    private SecurityConfigurationBuilder securityConfigurationBuilder;
    private SecurityConfiguration securityConfiguration;

    /**
     * <p>Veto all {@link org.picketlink.Identity} implementations.</p>
     *
     * <p>This is necessary in order to proper install the {@link org.picketlink.internal.IdentityBeanDefinition},
     * which is responsible for defining those beans.</p>
     *
     * @param pat
     * @param <T>
     */
    <T> void vetoIdentityImplementations(@Observes ProcessAnnotatedType<T> pat) {
        final AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        Class<T> javaClass = annotatedType.getJavaClass();

        if (!Identity.class.equals(javaClass) && Identity.class.isAssignableFrom(javaClass)) {
            pat.veto();
        }
    }

    /**
     * <p>Initializes the PicketLink configuration.</p>
     *
     * @param abd
     * @param beanManager
     */
    void installIdentityBean(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
        log.info(">>>>> install zanata identity bean definition");
        this.identityBeanDefinition = new ZanataIdentityBeanDefinition(beanManager);
        abd.addBean(identityBeanDefinition);
    }

    /**
     * <p>Initializes the PicketLink configuration.</p>
     *
     * @param adv
     * @param beanManager
     */
    void initializeConfiguration(@Observes AfterDeploymentValidation adv, BeanManager beanManager) {
        ROOT_LOGGER.picketlinkBootstrap();

        SecurityConfigurationEvent securityConfigurationEvent = new SecurityConfigurationEvent();

        beanManager.fireEvent(securityConfigurationEvent);

        // TODO: best is fire an event. We're not doing that because of issues in EAP and WildFly when using PL jars from modules.
        this.securityConfigurationBuilder = securityConfigurationEvent.getBuilder();

        this.securityConfiguration = this.securityConfigurationBuilder.build();

        this.identityBeanDefinition.setSecurityConfiguration(this.securityConfiguration);
    }

    public SecurityConfigurationBuilder getSecurityConfigurationBuilder() {
        return this.securityConfigurationBuilder;
    }

    public SecurityConfiguration getSecurityConfiguration() {
        return this.securityConfiguration;
    }
}
