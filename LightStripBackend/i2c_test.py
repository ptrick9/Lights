system = 'Pi'
channel = 1
ADDRESS = 0x66
bus = None

if system == 'Pi':
    import smbus
    bus = smbus.SMBus(channel)

data = [0]
data.extend(i for i in range(30))
bus.write_block_data(ADDRESS, 2, data)
data = [10]
data.extend(i for i in range(30,60))
bus.write_block_data(ADDRESS, 2, data)
