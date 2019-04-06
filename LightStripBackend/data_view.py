from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from data import Configuration, LightValues, Base

engine = create_engine('sqlite:///test.db')
Base.metadata.bind = engine
DBSession = sessionmaker(bind=engine)
session = DBSession()

c = session.query(Configuration).all()[2]

print(session.query(LightValues).filter(LightValues.config == c).all())