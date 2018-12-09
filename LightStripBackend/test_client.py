import asyncio
import websockets
import json

async def hello(uri):
    async with websockets.connect(uri) as websocket:
        #m = {'command' : 'list_configs'}
        m = {'command' : 'detail_config', 'data': {
            'configName' : 'christmas'
        }}
        await websocket.send(json.dumps(m))
        print('sent')
        d = await websocket.recv()
        print(d)

asyncio.get_event_loop().run_until_complete(
    hello('ws://localhost:8765'))