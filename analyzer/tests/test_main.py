from fastapi.testclient import TestClient
from main import app

client = TestClient(app)


def test_analyze_valid_payload_passes():
    payload = {
        "configId": "ACM-1001",
        "aircraftType": "A320",
        "softwareVersion": "1.2.3",
        "navDataCycle": "AIRAC-2024-08",
        "modules": [{"name": "FMS", "enabled": True}]
    }

    res = client.post("/analyze", json=payload)
    body = res.json()

    assert res.status_code == 200
    assert body["errors"] == []
    assert body["warnings"] == []


def test_analyze_flags_bad_inputs():
    payload = {
        "softwareVersion": "1.2",  # not semver
        "navDataCycle": "2024-08",  # not AIRAC
        "modules": [{"name": "Weather", "enabled": False}]
    }

    res = client.post("/analyze", json=payload)
    body = res.json()

    assert res.status_code == 200
    assert "softwareVersion missing or not semver" in body["errors"]
    assert "navDataCycle should match AIRAC-YYYY-NN" in body["warnings"]
    assert "FMS module not enabled" in body["warnings"]
