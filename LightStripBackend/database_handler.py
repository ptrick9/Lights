from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from data import Configuration, LightValues, Base

engine = create_engine('sqlite:///test.db')
Base.metadata.bind = engine
DBSession = sessionmaker(bind=engine)
session = DBSession()

new_config = Configuration(name='christmas')
session.add(new_config)
session.commit()

for i in range(100):
    new_light = LightValues(light_pos=i, color=0xff0000 if i%2==0 else 0x00ff00, config=new_config)
    session.add(new_light)
    session.commit()