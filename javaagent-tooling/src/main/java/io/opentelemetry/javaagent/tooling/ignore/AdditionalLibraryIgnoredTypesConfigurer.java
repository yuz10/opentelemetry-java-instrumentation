/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.tooling.ignore;

import com.google.auto.service.AutoService;
import io.opentelemetry.instrumentation.api.config.Config;
import io.opentelemetry.javaagent.extension.ignore.IgnoredTypesBuilder;
import io.opentelemetry.javaagent.extension.ignore.IgnoredTypesConfigurer;

/**
 * Additional global ignore settings that are used to reduce number of classes we try to apply
 * expensive matchers to.
 *
 * <p>This is separated from {@link GlobalIgnoredTypesConfigurer} to allow for better testing. The
 * idea is that we should be able to remove this matcher from the agent and all tests should still
 * pass. Moreover, no classes matched by this matcher should be modified during test run.
 */
@AutoService(IgnoredTypesConfigurer.class)
public class AdditionalLibraryIgnoredTypesConfigurer implements IgnoredTypesConfigurer {

  // We set this system property when running the agent with unit tests to allow verifying that we
  // don't ignore libraries that we actually attempt to instrument. It means either the list is
  // wrong or a type matcher is.
  private static final String ADDITIONAL_LIBRARY_IGNORES_ENABLED =
      "otel.javaagent.testing.additional-library-ignores.enabled";

  @Override
  public void configure(Config config, IgnoredTypesBuilder builder) {
    if (!config.getBooleanProperty(ADDITIONAL_LIBRARY_IGNORES_ENABLED, true)) {
      return;
    }

    builder
        .ignoreClass("com.beust.jcommander.")
        .ignoreClass("com.fasterxml.classmate.")
        .ignoreClass("com.github.mustachejava.")
        .ignoreClass("com.jayway.jsonpath.")
        .ignoreClass("com.lightbend.lagom.")
        .ignoreClass("javax.el.")
        .ignoreClass("org.apache.lucene.")
        .ignoreClass("org.apache.tartarus.")
        .ignoreClass("org.json.simple.")
        .ignoreClass("org.yaml.snakeyaml.");

    builder.ignoreClass("net.sf.cglib.").allowClass("net.sf.cglib.core.internal.LoadingCache$2");

    builder
        .ignoreClass("org.springframework.aop.")
        .ignoreClass("org.springframework.cache.")
        .ignoreClass("org.springframework.dao.")
        .ignoreClass("org.springframework.ejb.")
        .ignoreClass("org.springframework.expression.")
        .ignoreClass("org.springframework.format.")
        .ignoreClass("org.springframework.jca.")
        .ignoreClass("org.springframework.jdbc.")
        .ignoreClass("org.springframework.jmx.")
        .ignoreClass("org.springframework.jndi.")
        .ignoreClass("org.springframework.lang.")
        .ignoreClass("org.springframework.objenesis.")
        .ignoreClass("org.springframework.orm.")
        .ignoreClass("org.springframework.remoting.")
        .ignoreClass("org.springframework.scripting.")
        .ignoreClass("org.springframework.stereotype.")
        .ignoreClass("org.springframework.transaction.")
        .ignoreClass("org.springframework.ui.")
        .ignoreClass("org.springframework.validation.");

    builder
        .ignoreClass("org.springframework.data.")
        .allowClass("org.springframework.data.repository.core.support.RepositoryFactorySupport")
        .allowClass("org.springframework.data.convert.ClassGeneratingEntityInstantiator$")
        .allowClass("org.springframework.data.jpa.repository.config.InspectionClassLoader");

    builder
        .ignoreClass("org.springframework.amqp.")
        .allowClass("org.springframework.amqp.rabbit.connection.");

    builder
        .ignoreClass("org.springframework.beans.")
        .allowClass("org.springframework.beans.factory.support.DisposableBeanAdapter")
        .allowClass("org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader$");

    builder
        .ignoreClass("org.springframework.boot.")
        .allowClass("org.springframework.boot.context.web.")
        .allowClass("org.springframework.boot.logging.logback.")
        .allowClass("org.springframework.boot.web.filter.")
        .allowClass("org.springframework.boot.web.servlet.")
        .allowClass("org.springframework.boot.autoconfigure.BackgroundPreinitializer$")
        .allowClass("org.springframework.boot.autoconfigure.condition.OnClassCondition$")
        .allowClass("org.springframework.boot.web.embedded.netty.NettyWebServer$")
        .allowClass(
            "org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer$")
        .allowClass(
            "org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedWebappClassLoader")
        .allowClass("org.springframework.boot.context.embedded.EmbeddedWebApplicationContext")
        .allowClass(
            "org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext")
        // spring boot 2 classes
        .allowClass(
            "org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext")
        .allowClass(
            "org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext")
        .allowClass("org.springframework.boot.web.embedded.tomcat.TomcatWebServer$")
        .allowClass("org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader")
        .allowClass("org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean$")
        .allowClass("org.springframework.boot.StartupInfoLogger$")
        .allowClass("org.springframework.boot.SpringApplicationShutdownHook");

    builder
        .ignoreClass("org.springframework.cglib.")
        // This class contains nested Callable instance that we'd happily not touch, but
        // unfortunately our field injection code is not flexible enough to realize that, so instead
        // we instrument this Callable to make tests happy.
        .allowClass("org.springframework.cglib.core.internal.LoadingCache$");

    builder
        .ignoreClass("org.springframework.context.")
        // More runnables to deal with
        .allowClass("org.springframework.context.support.AbstractApplicationContext$")
        .allowClass("org.springframework.context.support.ContextTypeMatchClassLoader")
        // Allow instrumenting ApplicationContext implementations - to inject beans
        .allowClass("org.springframework.context.annotation.AnnotationConfigApplicationContext")
        .allowClass("org.springframework.context.support.AbstractApplicationContext")
        .allowClass("org.springframework.context.support.GenericApplicationContext");

    builder
        .ignoreClass("org.springframework.core.")
        .allowClass("org.springframework.core.task.")
        .allowClass("org.springframework.core.DecoratingClassLoader")
        .allowClass("org.springframework.core.OverridingClassLoader")
        .allowClass("org.springframework.core.ReactiveAdapterRegistry$EmptyCompletableFuture");

    builder
        .ignoreClass("org.springframework.instrument.")
        .allowClass("org.springframework.instrument.classloading.SimpleThrowawayClassLoader")
        .allowClass("org.springframework.instrument.classloading.ShadowingClassLoader");

    builder
        .ignoreClass("org.springframework.http.")
        // There are some Mono implementation that get instrumented
        .allowClass("org.springframework.http.server.reactive.");

    builder
        .ignoreClass("org.springframework.jms.")
        .ignoreClass("org.springframework.jms.listener.")
        .ignoreClass(
            "org.springframework.jms.config.JmsListenerEndpointRegistry$AggregatingCallback");

    builder
        .ignoreClass("org.springframework.messaging.")
        .allowClass("org.springframework.messaging.support.ExecutorSubscribableChannel$SendTask")
        .allowClass("org.springframework.messaging.support.MessageHandlingRunnable");

    builder
        .ignoreClass("org.springframework.util.")
        .allowClass("org.springframework.util.concurrent.");

    builder
        .ignoreClass("org.springframework.web.")
        .allowClass("org.springframework.web.servlet.")
        .allowClass("org.springframework.web.filter.")
        .allowClass("org.springframework.web.multipart.")
        .allowClass("org.springframework.web.reactive.")
        .allowClass("org.springframework.web.context.request.async.")
        .allowClass(
            "org.springframework.web.context.support.AbstractRefreshableWebApplicationContext")
        .allowClass("org.springframework.web.context.support.GenericWebApplicationContext")
        .allowClass("org.springframework.web.context.support.XmlWebApplicationContext");

    // xml-apis, xerces, xalan, but not xml web-services
    builder
        .ignoreClass("javax.xml.")
        .allowClass("javax.xml.ws.")
        .ignoreClass("org.apache.bcel.")
        .ignoreClass("org.apache.html.")
        .ignoreClass("org.apache.regexp.")
        .ignoreClass("org.apache.wml.")
        .ignoreClass("org.apache.xalan.")
        .ignoreClass("org.apache.xerces.")
        .ignoreClass("org.apache.xml.")
        .ignoreClass("org.apache.xpath.")
        .ignoreClass("org.xml.");

    builder
        .ignoreClass("ch.qos.logback.")
        // We instrument this Runnable
        .allowClass("ch.qos.logback.core.AsyncAppenderBase$Worker")
        // Allow instrumenting loggers & events
        .allowClass("ch.qos.logback.classic.Logger")
        .allowClass("ch.qos.logback.classic.spi.LoggingEvent")
        .allowClass("ch.qos.logback.classic.spi.LoggingEventVO");

    builder
        .ignoreClass("com.codahale.metrics.")
        // We instrument servlets
        .allowClass("com.codahale.metrics.servlets.");

    builder
        .ignoreClass("com.couchbase.client.deps.")
        // Couchbase library includes some packaged dependencies, unfortunately some of them are
        // instrumented by executors instrumentation
        .allowClass("com.couchbase.client.deps.io.netty.")
        .allowClass("com.couchbase.client.deps.org.LatencyUtils.")
        .allowClass("com.couchbase.client.deps.com.lmax.disruptor.");

    builder
        .ignoreClass("com.google.cloud.")
        .ignoreClass("com.google.instrumentation.")
        .ignoreClass("com.google.j2objc.")
        .ignoreClass("com.google.gson.")
        .ignoreClass("com.google.logging.")
        .ignoreClass("com.google.longrunning.")
        .ignoreClass("com.google.protobuf.")
        .ignoreClass("com.google.rpc.")
        .ignoreClass("com.google.thirdparty.")
        .ignoreClass("com.google.type.");

    builder
        .ignoreClass("com.google.common.")
        .allowClass("com.google.common.util.concurrent.")
        .allowClass("com.google.common.base.internal.Finalizer");

    builder
        .ignoreClass("com.google.inject.")
        // We instrument Runnable there
        .allowClass("com.google.inject.internal.AbstractBindingProcessor$")
        .allowClass("com.google.inject.internal.BytecodeGen$")
        .allowClass("com.google.inject.internal.cglib.core.internal.$LoadingCache$");

    builder.ignoreClass("com.google.api.").allowClass("com.google.api.client.http.HttpRequest");

    builder
        .ignoreClass("org.h2.")
        .allowClass("org.h2.Driver")
        .allowClass("org.h2.jdbc.")
        .allowClass("org.h2.jdbcx.")
        // Some runnables that get instrumented
        .allowClass("org.h2.util.Task")
        .allowClass("org.h2.store.FileLock")
        .allowClass("org.h2.engine.DatabaseCloser")
        .allowClass("org.h2.engine.OnExitDatabaseCloser");

    builder
        .ignoreClass("com.carrotsearch.hppc.")
        .allowClass("com.carrotsearch.hppc.HashOrderMixing$");

    builder
        .ignoreClass("com.fasterxml.jackson.")
        .allowClass("com.fasterxml.jackson.module.afterburner.util.MyClassLoader");

    // kotlin, note we do not ignore kotlinx because we instrument coroutines code
    builder.ignoreClass("kotlin.").allowClass("kotlin.coroutines.jvm.internal.DebugProbesKt");
  }
}
