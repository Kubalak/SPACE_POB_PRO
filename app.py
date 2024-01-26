from flask import Flask, request, jsonify
import math
from static.celestial_bodies import celestial_bodies
from skyfield.api import load, Topos, utc
import numpy as np
from datetime import datetime, timedelta
from DayNightCalculator.dayNightCalc import day_duration
from MoonPhaseCalculator.moonPhaseCalc import lunar_phase

app = Flask(__name__)

#funkcja która przy pomocy skyfield api określa położenie księzyca oraz słońca. Obserwator ustawiony na ziemi.
def get_moon_and_sun_positions(time):
    eph = load('de421.bsp')
    observer = Topos(latitude_degrees=0, longitude_degrees=0)

    ts = load.timescale()
    t = ts.utc(time)

    observer_at_time = eph['earth'] + observer
    moon_position = observer_at_time.at(t).observe(eph['moon']).apparent().position.km
    sun_position = observer_at_time.at(t).observe(eph['sun']).apparent().position.km

    return moon_position, sun_position #Zwracane są wektory pozycji Księzyca i Słońca

#funkcja która symuluje orbitę satelity
def simulate_orbit(initial_position, initial_velocity, start_time, time_steps, time_delta):
    G = 6.674 * (10 ** -11)
    M_earth = 5.972 * (10 ** 24)
    atmospheric_density = 1.2

    delta_t = 1.0
    positions = [initial_position]
    velocities = [initial_velocity]

    current_time = start_time
    for _ in range(time_steps):
        r = np.linalg.norm(positions[-1])
        gravitational_force_earth = -G * M_earth / r ** 3 * positions[-1]

        velocity = velocities[-1]
        speed = np.linalg.norm(velocity)
        atmospheric_force = -0.5 * atmospheric_density * speed ** 2 * velocity / speed

        #Uwzględnienie perturbacji grawitacyjnych od Księżyca i Słońca
        moon_position, sun_position = get_moon_and_sun_positions(current_time)
        gravitational_force_moon = -G * M_earth / np.linalg.norm(moon_position - positions[-1]) ** 3 * (
                moon_position - positions[-1])
        gravitational_force_sun = -G * M_earth / np.linalg.norm(sun_position - positions[-1]) ** 3 * (
                sun_position - positions[-1])

        gravitational_force = gravitational_force_earth + gravitational_force_moon + gravitational_force_sun

        acceleration = (gravitational_force + atmospheric_force) / M_earth
        new_velocity = velocities[-1] + acceleration * delta_t
        new_position = positions[-1] + new_velocity * delta_t

        positions.append(new_position)
        velocities.append(new_velocity)

        #Aktualizacja czasu na podstawie czasu delta
        current_time += time_delta

    return np.array(positions) #Zwracane jest tablica z kolejnymi pozycjami satelity w trakcie symulacji


GRAVITY = {
    'MERCURY': 3.7,
    'VENUS': 8.87,
    'EARTH': 9.81,
    'MARS': 3.721,
    'JUPITER': 24.79,
    'SATURN': 10.44,
    'URANUS': 8.69,
    'NEPTUNE': 11.15
}

def calculate_space_velocities(mass, radius):
    G = 6.67430e-11  # stała grawitacyjna
    v1 = math.sqrt(G * mass / radius)  # Pierwsza prędkość kosmiczna
    v2 = math.sqrt(2) * v1            # Druga prędkość kosmiczna
    return v1, v2

#ALGORYTM 5
@app.route('/space_velocities', methods=['POST'])
def space_velocities():
    """
    Calculates the first and second cosmic velocities for a given celestial body.

    Accepts a JSON with one of two sets of data:
    1. Name of the celestial body (body_name) - uses predefined values for mass and radius.
    2. Mass (mass) and radius (radius) of the celestial body - calculates velocities based on these data.

    Example request with the name of a celestial body:
    {
        "body_name": "Mars"
    }

    Example request with mass and radius:
    {
        "body_name": "some_body_name", (optional)
        "mass": 6.0e24,
        "radius": 6.4e6
    }

    Returns:
    A JSON object containing:
    - 'body_name': The name of the celestial body (if provided or null).
    - 'first_velocity': Calculated first cosmic velocity in meters per second.
    - 'second_velocity': Calculated second cosmic velocity in meters per second.

    In case of invalid input, returns a JSON object with an 'error' key and a 400 status code.
    """
    data = request.json
    body_name = data.get('body_name')
    mass = data.get('mass')
    radius = data.get('radius')

    if mass is None and radius is None:
        if body_name and body_name in celestial_bodies:
            body = celestial_bodies[body_name]
            mass, radius = body['mass'], body['radius']
        else:
            return jsonify({"error": "Either predefined celestial body name or custom mass and radius must be provided"}), 400
    elif mass is not None and radius is not None:
        if mass <= 0 or radius <= 0:
            return jsonify({"error": "Mass and radius must be positive numbers"}), 400
        # Check for unrealistic values
        if mass > 1e32 or radius > 1e9:
            return jsonify({"error": "Mass or radius values are unrealistically large"}), 400
    else:
        return jsonify({"error": "Incomplete data: both mass and radius are required for custom calculations"}), 400

    v1, v2 = calculate_space_velocities(mass, radius)

    return jsonify({"body_name": body_name, "first_velocity": v1, "second_velocity": v2})

#ALGORYTM 4
@app.route('/predict_orbit', methods=['POST'])
def predict_orbit():
    try:
        data = request.get_json()

        initial_position = np.array(data['initial_position'])
        initial_velocity = np.array(data['initial_velocity'])
        time_steps = data['time_steps']

        start_time_str = data.get('start_time', '2023-01-01T00:00:00')  #Jeśli start_time nie zostanie podany wtedy jest przypisana domyślna wartość
        start_time = datetime.fromisoformat(start_time_str).replace(tzinfo=utc)

        time_delta_minutes = data.get('time_delta_minutes', 1) #Jeśli time_delta nie zostanie podany wtedy jest przypisana domyślna wartość
        time_delta = timedelta(minutes=time_delta_minutes)

        orbit_positions = simulate_orbit(initial_position, initial_velocity, start_time, time_steps, time_delta)

        return jsonify({'orbit_positions': orbit_positions.tolist()}), 200

    except Exception as e:
        return jsonify({'error': str(e)}), 500

# ALGORYTM 3    
@app.route('/calculate_weight', methods=['POST'])
def calculate_weight():
    data = request.get_json()
    weight_on_earth = data.get('weight_on_earth')
    planet = data.get('planet').upper()

    if planet not in GRAVITY:
        return jsonify({'error': 'Invalid planet'}), 400
    earth_weight = weight_on_earth * GRAVITY['EARTH']
    weight_on_planet = (weight_on_earth / GRAVITY['EARTH']) * GRAVITY[planet]
    return jsonify({'weight_on_planet': round(weight_on_planet, 5)})

# ALGORYTM 1
@app.route('/distance/<planet_name>', methods=['GET'])
def calculate_distance(planet_name):
    planet_name = planet_name.capitalize()

    # Stała grawitacyjna (m^3/kg/s^2)
    G = 6.67430e-11

    # Masa Słońca (kg)
    m_s = 1.989e30

    # Wartość jednostki astronomicznej (AU) w kilometrach
    au_in_km = 149.6e6

    # Okres obiegu i przyspieszenie grawitacyjne dla poszczególnych planet
    planet_data = {
        'Merkury': {'period': 7600526, 'gravity': 3.7},
        'Wenus': {'period': 19413907, 'gravity': 8.87},
        'Ziemia': {'period': 31558118, 'gravity': 9.81},
        'Mars': {'period': 59354294, 'gravity': 3.71},
        'Jowisz': {'period': 374335776, 'gravity': 24.79},
        'Saturn': {'period': 929596608, 'gravity': 10.44},
        'Uran': {'period': 2650461899, 'gravity': 8.69},
        'Neptun': {'period': 5200418560, 'gravity': 11.15}
    }

    if planet_name not in planet_data:
        return jsonify({'error': 'Planeta nie znaleziona'}), 404

    planet_info = planet_data[planet_name]
    T = planet_info['period']
    g = planet_info['gravity']

    r_m = ((G * m_s * T ** 2) / (4 * math.pi ** 2)) ** (1 / 3)
    r_km = r_m / 1000
    r_au = r_km / au_in_km

    result = {
        'planet': planet_name,
        'distance_km': round(r_km, 2),
        'distance_au': round(r_au, 2)
    }

    return jsonify(result)

# Algorytm 2
@app.route('/lunar_phase', methods=['POST'])
def lunar_phase_calculator():
    data = request.get_json()
    x = data.get('date')
    response = lunar_phase(x)
    return response


@app.route('/day_length', methods=['POST'])
def day_length_calculator():
    data = request.get_json()
    response = day_duration(data.get('date'), data.get('lat'), data.get('lon'))
    return response


if __name__ == "__main__":
    app.run(debug=True)



if __name__ == '__main__':
    app.run(debug=True)
