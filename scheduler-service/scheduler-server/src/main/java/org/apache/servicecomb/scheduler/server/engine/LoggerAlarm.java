package org.apache.servicecomb.scheduler.server.engine;

import org.apache.servicecomb.scheduler.common.AlarmContext;
import org.apache.servicecomb.scheduler.common.IAlarm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggerAlarm implements IAlarm {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerAlarm.class);

    @Override
    public void sendAlarm(AlarmContext alarmContext) {
        LOGGER.warn("alarm: [{}] [{}] [{}] ", alarmContext.getAlarmLevel(), alarmContext.getJobContext(), alarmContext.getAlarmMessage());
    }
}
