import asyncio
import websockets
import json

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from data import Configuration, LightValues, Base


engine = create_engine('sqlite:///test.db')
Base.metadata.bind = engine
DBSession = sessionmaker(bind=engine)
session = DBSession()


def getConfigs():
    q = session.query(Configuration).all()
    resp = {'command': 'list', 'data': []}
    for con in q:
        resp['data'].append(con.name)
    print(resp)
    return json.dumps(resp)


def detailConfig(d):
    data = d['data']
    config = session.query(Configuration).filter(Configuration.name == data['configName'])[0]
    q = session.query(LightValues).filter(LightValues.config == config).all()
    lights = []
    for l in q:
        lights.append({'pos': l.light_pos, 'color': l.color})

    resp = {'command': 'detail', 'data': {
        'configName': data['configName'],
        'lightValues': lights
    }}
    return json.dumps(resp)




async def echo(websocket, path):
    data = await websocket.recv()
    print(data)
    d = json.loads(data)
    message = d['command']

    if message == 'brightness':
        print('bright enter')
        pass
    elif message == 'list_configs':
        await websocket.send(getConfigs())
        pass
    elif message == 'detail_config':
        await websocket.send(detailConfig(d))
        print('detail enter')
        pass
    elif message == 'edit_config':
        print('message enter')
        pass
    elif message == 'new_config':
        print('new enter')
        pass

asyncio.get_event_loop().run_until_complete(websockets.serve(echo, 'localhost', 8765))
asyncio.get_event_loop().run_forever()