from datetime import datetime


def lunar_phase(date):
    """
    The function calculates the lunar phase based on the provided date.

    Args:
        date (str): Date in the format YYYY-MM-DD.

    Returns:
        object: A dictionary containing the lunar phase name if the calculations succeed.
        If an error occurs, it returns a dictionary with the key "error" and a description of the error.
    """

    try:
        date_object = datetime.strptime(date, '%Y-%m-%d')
        year, month, day = date_object.year, date_object.month, date_object.day
        threshold = datetime.strptime(f'{year}-{month}-{day}', '%Y-%m-%d')
        phase = calc_before_2000(date) if date_object < threshold else calc_after_2000(date)
        phase_name = (
            "First Quarter" if 1 < phase < 48 else
            "Full Moon" if 48 <= phase <= 52 else
            "Last Quarter" if 52 < phase < 99 else
            "New Moon" if phase <= 1 or phase >= 99 else None
        )
        return {"phase": phase_name}

    except (ValueError, Exception) as e:
        return {"error": str(e)}


def calc_after_2000(date: str):
    date = datetime.strptime(date, '%Y-%m-%d')
    base_date = datetime(2000, 1, 6)
    synodic_month = 29.53058867
    phase = (((date - base_date).days % synodic_month) / synodic_month) * 100
    return phase


def calc_before_2000(date: str):
    date = datetime.strptime(date, '%Y-%m-%d')
    A = int(date.year / 100)
    B = A // 4
    C = 2 - A + B
    JD = C + date.day + int(365.25 * (date.year + 4716)) + int(30.6001 * (date.month + 1)) - 1524.5
    phase = ((JD - 2451549.5) / 29.53 % 1) * 100
    return phase


