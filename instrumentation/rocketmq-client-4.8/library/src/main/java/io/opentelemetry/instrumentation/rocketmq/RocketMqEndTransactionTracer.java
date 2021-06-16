/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.rocketmq;

import static io.opentelemetry.api.trace.SpanKind.PRODUCER;
import static io.opentelemetry.instrumentation.rocketmq.TextMapExtractAdapter.GETTER;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.tracer.BaseTracer;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import org.apache.rocketmq.client.hook.EndTransactionContext;
import org.apache.rocketmq.common.message.Message;

final class RocketMqEndTransactionTracer extends BaseTracer {

  private final boolean propagationEnabled;

  RocketMqEndTransactionTracer(
      OpenTelemetry openTelemetry,
      boolean propagationEnabled) {
    super(openTelemetry);
    this.propagationEnabled = propagationEnabled;
  }

  @Override
  protected String getInstrumentationName() {
    return "io.opentelemetry.javaagent.rocketmq-client";
  }

  Context startSpan(Context parentContext, Message msg, EndTransactionContext context) {
    SpanBuilder spanBuilder = startSpanBuilder(extractParent(msg), msg, context);
    return withConsumerSpan(parentContext, spanBuilder.startSpan());
  }

  private SpanBuilder startSpanBuilder(Context parentContext, Message msg, EndTransactionContext context) {
    SpanBuilder spanBuilder =
        spanBuilder(parentContext, spanNameOnEndTransction(msg), PRODUCER)
            .setAttribute(SemanticAttributes.MESSAGING_SYSTEM, "rocketmq")
            .setAttribute(SemanticAttributes.MESSAGING_DESTINATION, msg.getTopic())
            .setAttribute(SemanticAttributes.MESSAGING_DESTINATION_KIND, "topic")
            .setAttribute(SemanticAttributes.MESSAGING_OPERATION, "endTransaction")
            .setAttribute(
                SemanticAttributes.MESSAGING_MESSAGE_PAYLOAD_SIZE_BYTES,
                (long) msg.getBody().length);
    spanBuilder.setAttribute("messaging.rocketmq.tags", msg.getTags());
    spanBuilder.setAttribute("messaging.rocketmq.keys", msg.getKeys());
    spanBuilder.setAttribute("messaging.rocketmq.broker_address", context.getBrokerAddr());
    spanBuilder.setAttribute("messaging.rocketmq.msg_id", context.getMsgId());
    spanBuilder.setAttribute("messaging.rocketmq.transaction_id", context.getTransactionId());
    spanBuilder.setAttribute("messaging.rocketmq.is_from_transaction_check", context.isFromTransactionCheck());
    spanBuilder.setAttribute("messaging.rocketmq.transaction_state", context.getTransactionState().name());
    return spanBuilder;
  }

  private Context extractParent(Message msg) {
    if (propagationEnabled) {
      return extract(msg.getProperties(), GETTER);
    } else {
      return Context.current();
    }
  }

  private static String spanNameOnEndTransction(Message msg) {
    return msg.getTopic() + " endTransaction";
  }

}
