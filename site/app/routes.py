from flask import render_template, flash, redirect, url_for
from app import app
from app.forms import ColorsForm

@app.route('/')
@app.route('/index')

def index():
    config = {'name': 'default',
              'colors': [0x20, 0x3, 0x1234, 0x654321]}

    return render_template('index.html', title='Configurator', config=config)


@app.route('/login', methods=['GET', 'POST'])
def login():

    color_values = [{'color': 0},
                    {'color': 1}]
    form = ColorsForm(colors=color_values)
    if form.validate_on_submit():
        flash('Login requested for user {}, remember_me={}'.format(
            form.config_name.data, form.colors.data))
        return redirect(url_for('index'))
    return render_template('login.html', title='Create Colors', form=form)

