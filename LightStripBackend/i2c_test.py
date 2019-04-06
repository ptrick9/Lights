system = 'Pi'
channel = 1
ADDRESS = 0x66
bus = None

if system == 'Pi':
    import smbus
    bus = smbus.SMBus(channel)

data = 65
bus.write_byte_data(ADDRESS, 0, data)
