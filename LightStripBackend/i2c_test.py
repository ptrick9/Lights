system = 'Pi'
channel = 1
ADDRESS = 0x66
bus = None

if system == 'Pi':
    import smbus
    bus = smbus.SMBus(channel)

data = 65
bus.write_block_data(ADDRESS, 2, [0].extend(i for i in range(30)))
bus.write_block_data(ADDRESS, 2, [10].extend(i for i in range(30)))
