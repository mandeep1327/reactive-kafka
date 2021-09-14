package net.apmoller.crb.microservices.external.apis.dcsa.processor.metric;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MetricUtils {

    public static final String DUMMY_VALUE = "default_value";
    public static final String ENV_TAG_NAME = "env";
    public static final String EXTERNAL_RECIEVE_DCSA_GEMS_EVENT = "external.dcsa.gems_events.total";
    public static final String EXTERNAL_RECIEVE_DCSA_EVENT_TYPE = "external.dcsa.event.recieved.total";
    public static final String EXTERNAL_DCSA_EVENT_DROPPED = "external.dcsa.event.dropped.total";
    public static final String EXTERNAL_DCSA_EVENT_ERROR = "external.dcsa.event.error.total";
    public static final String EXTERNAL_RECIEVE_KAFKA_MESSAGE = "external.dcsa.kafka.recieved.total";
    public static final String EXTERNAL_KAFKA_ERROR = "external.dcsa.kafka.error.total";

}
