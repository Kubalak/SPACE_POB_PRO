from math import sin, cos, pi, asin, acos
from datetime import datetime


def day_duration(date, latitude, longitude):
    """
    The algorithm for this function calculates the duration of the day based on geographic coordinates.

    Args:
        date (str): Date in format YYYY-MM-DD.
        latitude (double): Latitude, where positive values indicate the northern hemisphere and negative values indicate the southern hemisphere.
        longitude (double): Longitude, where positive values indicate east longitude and negative values indicate west longitude.

    Returns:
        object: A dictionary containing the day duration in hours and minutes if the calculations succeed.
        If an error occurs, it returns a dictionary with the key "error" and a description of the error.

    """
    try:
        date = datetime.strptime(date, '%Y-%m-%d')
        year, month, day = date.year, date.month, date.day
        rad = pi / 180
        astronomy_day = -0.833

        age = (367 * year - int(7 * (year + int((month + 9) / 12)) / 4) + int(275 * month / 9) + day - 730531.5) / 36525
        a = ((4.8949504201433 + 628.331969753199 * age) % (2 * pi))
        b = ((6.2400408 + 628.3019501 * age) % (2 * pi))
        c = 0.409093 - 0.0002269 * age
        d = 0.033423 * sin(b) + 0.00034907 * sin(2 * b)
        f = 0.0430398 * sin(2 * (a + d)) - 0.00092502 * sin(4 * (a + d)) - d
        g = asin(sin(c) * sin(a + d))
        h = (sin(rad * astronomy_day) - sin(rad * latitude) * sin(g)) / (cos(rad * latitude) * cos(g))
        sunrise = (pi - (f + rad * longitude + acos(h))) / (15 * rad)
        sunset = (pi - (f + rad * longitude - acos(h))) / (15 * rad)
        day_length = (sunset - sunrise) * 60

        return {"day_duration": {"hour": int(day_length // 60), "minute": int(day_length % 60)}}

    except (ValueError, Exception) as e:
        return {"error": str(e)}
