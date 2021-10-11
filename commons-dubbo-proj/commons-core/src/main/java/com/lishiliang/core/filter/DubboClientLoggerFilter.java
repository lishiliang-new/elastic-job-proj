package com.lishiliang.core.filter;

import com.alibaba.fastjson.JSON;
import com.lishiliang.core.utils.Context;
import com.lishiliang.core.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Activate(group = {Constants.CONSUMER}, order = -9999)
public class DubboClientLoggerFilter implements Filter {

    private final static Logger logger = LoggerFactory.getLogger(DubboClientLoggerFilter.class);

    private static final String TRACE_ID = "traceId";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //获取当前traceId并放入客户端
        RpcContext.getContext().setAttachment(TRACE_ID, Context.getCurrentContextTraceId());

        Result result = null;
        Long takeTime = 0L;
        Long startTime = System.currentTimeMillis();

        String traceId = MDC.get(TRACE_ID);
        if (StringUtils.isNotBlank(traceId)) {
            RpcContext.getContext().setAttachment(TRACE_ID, traceId);
        }else {

            traceId = Utils.generateUUIDWithMD5();
            RpcContext.getContext().setAttachment(TRACE_ID, traceId);
        }

        logger.info("Invoke RPC service. traceId : {}", traceId);
        try {
            result = invoker.invoke(invocation);
        } finally {
            takeTime = System.currentTimeMillis() - startTime;
            logger.info(String.format("Result Info:%s|IN:%s|OUT:%s|%dms", invocation.getMethodName(), JSON.toJSONString(invocation.getArguments()), Utils.abbreviate(JSON.toJSONString(result), 1024), takeTime));
        }
        return result;
    }

}
