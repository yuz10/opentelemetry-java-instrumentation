/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.rocketmq;

import io.opentelemetry.context.Context;
import org.apache.rocketmq.client.hook.EndTransactionContext;
import org.apache.rocketmq.client.hook.EndTransactionHook;

final class TracingEndTransactionHookImpl implements EndTransactionHook {

  private final RocketMqEndTransactionTracer tracer;

  TracingEndTransactionHookImpl(RocketMqEndTransactionTracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public String hookName() {
    return "OpenTelemetryEndTransactionTraceHook";
  }

  @Override
  public void endTransaction(EndTransactionContext context) {
    if (context == null || context.getMessage() == null) {
      return;
    }
    Context otelContext = tracer.startSpan(Context.current(), context.getMessage(), context);
    tracer.end(otelContext);
  }
}
