package org.zanata.security;

import org.picketlink.Identity;
import org.picketlink.config.SecurityConfiguration;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.*;
import javax.enterprise.util.AnnotationLiteral;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by pahuang on 7/10/15.
 */
public class ZanataIdentityBeanDefinition implements Bean<ZanataIdentity>, Serializable, PassivationCapable {

    private static final long serialVersionUID = -4725126763788040967L;

    private final BeanManager beanManager;
    private final InjectionTarget<ZanataIdentity> injectionTarget;
    private SecurityConfiguration securityConfiguration;

    public ZanataIdentityBeanDefinition(BeanManager beanManager) {
        this.beanManager = beanManager;

        AnnotatedType<ZanataIdentity> annotatedType = this.beanManager.createAnnotatedType(getBeanClass());
        this.injectionTarget = this.beanManager.createInjectionTarget(annotatedType);
    }

    public void setSecurityConfiguration(SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = securityConfiguration;
    }

    @Override
    public Set<Type> getTypes() {
        Set<Type> types = new HashSet<Type>();

        types.add(Identity.class);
        types.add(ZanataIdentity.class);
        types.add(Object.class);

        return types;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        Set<Annotation> qualifiers = new HashSet<Annotation>();

        qualifiers.add(new AnnotationLiteral<Default>() {
        });
        qualifiers.add(new AnnotationLiteral<Any>() {
        });

        return qualifiers;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        Class<? extends Annotation> scope = SessionScoped.class;

        if (this.securityConfiguration != null && this.securityConfiguration.getIdentityBeanConfiguration() != null) {
            scope = this.securityConfiguration.getIdentityBeanConfiguration().getScope();
        }

        if (scope == null) {
            throw new IllegalStateException("No scope defined for " + Identity.class.getSimpleName() + " bean. Check your configuration.");
        }

        return scope;
    }

    @Override
    public String getName() {
        return "identity";
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public Class<ZanataIdentity> getBeanClass() {
        return ZanataIdentity.class;
    }

    @Override
    public boolean isAlternative() {
        return true;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return this.injectionTarget.getInjectionPoints();
    }

    @Override
    public ZanataIdentity create(CreationalContext<ZanataIdentity> creationalContext) {
        ZanataIdentity identity = this.injectionTarget.produce(creationalContext);

        this.injectionTarget.inject(identity, creationalContext);
        this.injectionTarget.postConstruct(identity);

        return identity;
    }

    @Override
    public void destroy(ZanataIdentity instance, CreationalContext<ZanataIdentity> creationalContext) {
        this.injectionTarget.preDestroy(instance);
        this.injectionTarget.dispose(instance);
        creationalContext.release();
    }

    @Override
    public String getId() {
        return ZanataIdentityBeanDefinition.class.getName();
    }
}
