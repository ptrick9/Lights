import asyncio
import websockets
import json

async def hello(uri):
    async with websockets.connect(uri) as websocket:
        #m = {'command' : 'list_configs'}
        '''
        m = {'command' : 'detail_config', 'data': {
            'configName' : 'christmas'
        }}
        '''
        m = {'command' : 'new_config', 'data': {
            'configName' : 'new_test', 'lightValues': [{'pos': 0, 'color': 0xff0000}, {'pos': 1, 'color': 0x00ff00}]
        }}

        '''
        m = {'command' : 'delete_config', 'data': {
            'configName' : 'new_test'
        }}
        '''
        '''
        m = {'command': 'edit_config', 'data': {
            'configName': 'christmas', 'lightValues': [{'pos': 0, 'color': 0x0}, {'pos': 1, 'color': 0x1}]
        }}'''


        await websocket.send(json.dumps(m))
        print('sent')
        d = await websocket.recv()
        print(d)

asyncio.get_event_loop().run_until_complete(
    hello('ws://192.168.1.217:8765'))