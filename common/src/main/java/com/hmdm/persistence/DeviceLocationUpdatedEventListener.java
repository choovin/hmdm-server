/*
 *
 * Headwind MDM: Open Source Android MDM Software
 * https://h-mdm.com
 *
 * Copyright (C) 2019 Headwind Solutions LLC (http://h-sms.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hmdm.persistence;

import com.hmdm.event.DeviceLocationUpdatedEvent;
import com.hmdm.event.EventListener;
import com.hmdm.event.EventType;
import com.hmdm.persistence.domain.DeviceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>A listener for {@link EventType#DEVICE_LOCATION_UPDATED} events.</p>
 * <p>Persists device location updates to the database.</p>
 */
public class DeviceLocationUpdatedEventListener implements EventListener<DeviceLocationUpdatedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DeviceLocationUpdatedEventListener.class);

    private final DeviceLocationDAO deviceLocationDAO;

    /**
     * <p>Constructs new <code>DeviceLocationUpdatedEventListener</code> instance.</p>
     */
    public DeviceLocationUpdatedEventListener(DeviceLocationDAO deviceLocationDAO) {
        this.deviceLocationDAO = deviceLocationDAO;
    }

    /**
     * <p>Handles the event by persisting the location data.</p>
     *
     * @param event an event fired from the external source.
     */
    @Override
    public void onEvent(DeviceLocationUpdatedEvent event) {
        final int deviceId = event.getDeviceId();
        final List<com.hmdm.rest.json.DeviceLocation> locations = event.getLocations();

        if (locations == null || locations.isEmpty()) {
            return;
        }

        try {
            for (com.hmdm.rest.json.DeviceLocation jsonLocation : locations) {
                DeviceLocation location = new DeviceLocation();
                location.setDeviceId(deviceId);
                location.setLatitude(jsonLocation.getLat());
                location.setLongitude(jsonLocation.getLon());
                location.setTimestamp(jsonLocation.getTs());
                location.setProvider("gps"); // Default provider

                deviceLocationDAO.insertDeviceLocation(location);
            }

            logger.debug("Persisted {} location(s) for device {}", locations.size(), deviceId);
        } catch (Exception e) {
            logger.error("Failed to persist location update for device {}: {}", deviceId, e.getMessage(), e);
        }
    }

    /**
     * <p>Gets the type of supported events.</p>
     *
     * @return a type of supported events.
     */
    @Override
    public EventType getSupportedEventType() {
        return EventType.DEVICE_LOCATION_UPDATED;
    }
}
