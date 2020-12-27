#include <stdio.h>
 
#include "platform/Callback.h"
#include "events/EventQueue.h"
#include "platform/NonCopyable.h"
 
#include "ble/BLE.h"
#include "ble/Gap.h"
#include "ble/GattClient.h"
#include "ble/GattServer.h"
#include "ble_process.h"
 
using mbed::callback;
 
/**
 * BLE Human Interface Device service with a Angle Characterisic returning 
 * the angle of a potentiometer connected to analog input 0.
 */
class HidService {
    typedef HidService Self;
 
public:
    HidService() :
        _angle_char(0x2763, 0),
        _hid_service(
            /* uuid */ 0x1812,
            /* characteristics */ _hid_characteristics,
            /* numCharacteristics */ sizeof(_hid_characteristics) /
                                     sizeof(_hid_characteristics[0])
        ),
        _server(NULL),
        _event_queue(NULL),
        _angle_sensor(A0),
        _heartbeat_led(LED1)
    {
        // update internal pointers (value, descriptors and characteristics array)
        _hid_characteristics[0] = &_angle_char;
    }
 
    void start(BLE &ble_interface, events::EventQueue &event_queue)
    {
         if (_event_queue) {
            return;
        }
 
        _server = &ble_interface.gattServer();
        _event_queue = &event_queue;
 
        // register the service
        printf("Adding service\r\n");
        ble_error_t err = _server->addService(_hid_service);
 
        if (err) {
            printf("Error %u during service registration.\r\n", err);
            return;
        }
 
        // read write handler
        _server->onDataSent(as_cb(&Self::when_data_sent));
        _server->onDataRead(as_cb(&Self::when_data_read));
 
        // updates subscribtion handlers
        _server->onUpdatesEnabled(as_cb(&Self::when_update_enabled));
        _server->onUpdatesDisabled(as_cb(&Self::when_update_disabled));
 
        // print the handles
        printf("human interface device service registered\r\n");
        printf("service handle: %u\r\n", _hid_service.getHandle());
        printf("\angle characteristic value handle %u\r\n", _angle_char.getValueHandle());
 
        _event_queue->call_every(1000 /* ms */, callback(this, &Self::read_angle));
        _event_queue->call_every(1000 /* ms */, callback(this, &Self::blink_led));
    }
 
private:
 
    /**
     * Handler called when a notification or an indication has been sent.
     */
    void when_data_sent(unsigned count)
    {
        printf("sent %u updates\r\n", count);
    }
 
    /**
     * Handler called after an attribute has been read.
     */
    void when_data_read(const GattReadCallbackParams *e)
    {
        printf("data read:\r\n");
        printf("\tconnection handle: %u\r\n", e->connHandle);
        printf("\tattribute handle: %u", e->handle);
        if (e->handle == _angle_char.getValueHandle()) {
            printf(" (angle characteristic)\r\n");
        } else {
            printf("\r\n");
        }
    }
 
    /**
     * Handler called after a client has subscribed to notification or indication.
     *
     * @param handle Handle of the characteristic value affected by the change.
     */
    void when_update_enabled(GattAttribute::Handle_t handle)
    {
        printf("update enabled on handle %d\r\n", handle);
    }
 
    /**
     * Handler called after a client has cancelled his subscription from
     * notification or indication.
     *
     * @param handle Handle of the characteristic value affected by the change.
     */
    void when_update_disabled(GattAttribute::Handle_t handle)
    {
        printf("update disabled on handle %d\r\n", handle);
    }
 
    /**
     * Increment the second counter.
     */
    void read_angle(void)
    {
        uint8_t angle = (uint8_t) map(_angle_sensor.read(), 0, 1, 0, 360);
 
        ble_error_t err = _angle_char.set(*_server, angle);
        if (err) {
            printf("write of the angle value returned error %u\r\n", err);
            return;
        }
    }

    /**
     * Map a value in a range to another range
     */
    float map(float x, float in_min, float in_max, float out_min, float out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    /**
     * Blink the heartbeat led.
     */
    void blink_led(void)
    {
        _heartbeat_led = !_heartbeat_led;
    }
 
private:
    /**
     * Helper that construct an event handler from a member function of this
     * instance.
     */
    template<typename Arg>
    FunctionPointerWithContext<Arg> as_cb(void (Self::*member)(Arg))
    {
        return makeFunctionPointer(this, member);
    }
 
    /**
     * Read, Indicate  Characteristic declaration helper.
     *
     * @tparam T type of data held by the characteristic.
     */
    template<typename T>
    class ReadNotifyCharacteristic : public GattCharacteristic {
    public:
        /**
         * Construct a characteristic that can be read and emit
         * notification.
         *
         * @param[in] uuid The UUID of the characteristic.
         * @param[in] initial_value Initial value contained by the characteristic.
         */
        ReadNotifyCharacteristic(const UUID & uuid, const T& initial_value) :
            GattCharacteristic(
                /* UUID */ uuid,
                /* Initial value */ &_value,
                /* Value size */ sizeof(_value),
                /* Value capacity */ sizeof(_value),
                /* Properties */ GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_READ |
                                GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY,
                /* Descriptors */ NULL,
                /* Num descriptors */ 0,
                /* variable len */ false
            ),
            _value(initial_value) {
        }
 
        /**
         * Get the value of this characteristic.
         *
         * @param[in] server GattServer instance that contain the characteristic
         * value.
         * @param[in] dst Variable that will receive the characteristic value.
         *
         * @return BLE_ERROR_NONE in case of success or an appropriate error code.
         */
        ble_error_t get(GattServer &server, T& dst) const
        {
            uint16_t value_length = sizeof(dst);
            return server.read(getValueHandle(), &dst, &value_length);
        }
 
        /**
         * Assign a new value to this characteristic.
         *
         * @param[in] server GattServer instance that will receive the new value.
         * @param[in] value The new value to set.
         * @param[in] local_only Flag that determine if the change should be kept
         * locally or forwarded to subscribed clients.
         */
        ble_error_t set(
            GattServer &server, const uint8_t &value, bool local_only = false
        ) const {
            return server.write(getValueHandle(), &value, sizeof(value), local_only);
        }
 
    private:
        uint8_t _value;
    };
 
    ReadNotifyCharacteristic<uint8_t> _angle_char;
 
    // list of the characteristics of the hid service
    GattCharacteristic* _hid_characteristics[3];
 
    // service
    GattService _hid_service;
 
    GattServer* _server;
    events::EventQueue *_event_queue;

    AnalogIn _angle_sensor;

    DigitalOut _heartbeat_led;
};
 
int main() {
    BLE &ble_interface = BLE::Instance();
    events::EventQueue event_queue;
    HidService hid_service;
    BLEProcess ble_process(event_queue, ble_interface);
 
    ble_process.on_init(callback(&hid_service, &HidService::start));
 
    // bind the event queue to the ble interface, initialize the interface
    // and start advertising
    ble_process.start();
 
    // Process the event queue.
    event_queue.dispatch_forever();
 
    return 0;
}
 