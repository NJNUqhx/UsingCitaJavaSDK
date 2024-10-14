package service;

import com.citahub.cita.abi.EventValues;
import com.citahub.cita.abi.datatypes.Type;
import com.citahub.cita.protocol.core.methods.response.Log;

import java.util.List;

public class EventValuesWithLog {
    private final EventValues eventValues;
    private final Log log;

    EventValuesWithLog(EventValues eventValues, Log log) {
        this.eventValues = eventValues;
        this.log = log;
    }

    public List<Type> getIndexedValues() {
        return eventValues.getIndexedValues();
    }

    public List<Type> getNonIndexedValues() {
        return eventValues.getNonIndexedValues();
    }

    public Log getLog() {
        return log;
    }
}
