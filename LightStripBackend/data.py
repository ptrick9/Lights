from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.orm import relationship

engine = create_engine('sqlite:///test.db')

Base = declarative_base()

class Configuration(Base):
    __tablename__ = 'configurations'

    id = Column(Integer, primary_key=True)
    name = Column(String)

    def __repr__(self):
        return "<Config(id=%d name=%s" % (self.id, self.name)

class LightValues(Base):
    __tablename__ = 'colors'

    id = Column(Integer, primary_key=True)
    light_pos = Column(Integer)
    color = Column(Integer)
    config_id = Column(Integer, ForeignKey('configurations.id'))
    config = relationship(Configuration)

    def __repr__(self):
        return "<Light(pos=%d color=%d config=%s" % (self.light_pos, self.color, self.config.name)

Base.metadata.create_all(engine)




