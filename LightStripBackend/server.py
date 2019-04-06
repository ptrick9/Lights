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


selected = ''

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


def deleteConfig(d):
    data = d['data']

    config = session.query(Configuration).filter(Configuration.name == data['configName'])[0]
    lights = session.query(LightValues).filter(LightValues.config == config).all()

    for l in lights:
        session.delete(l)
    session.delete(config)
    session.commit()

    return json.dumps({'messageSuccess': 'True'})


def editConfig(d):
    data = d['data']
    config = session.query(Configuration).filter(Configuration.name == data['configName'])[0]

    lights = session.query(LightValues).filter(LightValues.config == config).all()

    for l in lights:
        session.delete(l)
    session.commit()

    lights = d['data']['lightValues']

    i = 0

    for ll in lights:
        new_light = LightValues(light_pos=i, color=ll['color'] & 0xffffff, config=config)
        session.add(new_light)
        session.commit()
        i += 1
    return json.dumps({'messageSuccess': 'True'})



def newConfig(d):

    n = d['data']['configName']
    new_config = Configuration(name=n)
    session.add(new_config)
    session.commit()

    lights = d['data']['lightValues']

    i = 0

    for ll in lights:
        new_light = LightValues(light_pos=i, color=ll['color'] & 0xffffff, config=new_config)
        session.add(new_light)
        session.commit()
        i += 1
    return json.dumps({'messageSuccess': 'True'})

def select(d):
    n = d['data']['config']
    print("selected %s" % n)
    global selected
    selected = n

def power(d):
    state = d['data']['state']
    print("Powering: %s" % state)

def extract_channels(color):
    r = (0xff0000 & color) >> 16
    g = (0xff00 & color) >> 8
    b = (0xff & color)

    return r, g, b

def rebuild_color(r, g, b):
    color = (r << 16) | (g << 8) | b

    return color

def brightness(d):
    level = d['data']['level']
    print("CONFIG CHANGE: %s" % selected)
    config = session.query(Configuration).filter(Configuration.name == selected)[0]

    lights = session.query(LightValues).filter(LightValues.config == config).all()

    for ll in lights:
        print(ll.color)
        r, g, b = extract_channels(ll.color)
        r = int(r * level)
        g = int(g * level)
        b = int(b * level)
        color = rebuild_color(r, g, b)
        print(color)





async def echo(websocket, path):
    data = await websocket.recv()
    print(data)
    d = json.loads(data)
    message = d['command']

    if message == 'list_configs':
        await websocket.send(getConfigs())
        print("sent")
        pass
    elif message == 'detail_config':
        await websocket.send(detailConfig(d))
        print('detail enter')
        pass
    elif message == 'edit_config':
        print('message enter')
        await websocket.send(editConfig(d))
        pass
    elif message == 'delete_config':
        print('delete')
        await websocket.send(deleteConfig(d))
        pass
    elif message == 'new_config':
        print('new enter')
        await websocket.send(newConfig(d))
        pass
    elif message == 'select':
        print("selected")
        select(d)
    elif message == 'power':
        print("power")
        power(d)
    elif message == 'brightness':
        print("bright")
        brightness(d)
    else:
        print("invalid command")

import socket
def get_ip_address():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(("8.8.8.8", 80))
    print(s.getsockname()[0])
    return s.getsockname()[0]

asyncio.get_event_loop().run_until_complete(websockets.serve(echo, get_ip_address(), 8765))
#asyncio.get_event_loop().run_until_complete(websockets.serve(echo, '127.0.0.1', 8765))
asyncio.get_event_loop().run_forever()