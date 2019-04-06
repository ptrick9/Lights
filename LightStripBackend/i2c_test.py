system = 'PC'
channel = 1
ADDRESS = 0x60
bus = None

if system == 'Pi':
    import smbus
    bus = smbus.SMBus(channel)

data = 45
bus.write_byte_data(80, 0, data)
