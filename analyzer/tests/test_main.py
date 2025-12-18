import pytest
from httpx import ASGITransport, AsyncClient
from main import app


@pytest.fixture(scope="module")
async def client():
    async with AsyncClient(transport=ASGITransport(app=app), base_url="http://testserver") as ac:
        yield ac


@pytest.mark.anyio
async def test_analyze_valid_payload_passes(client):
    payload = {
        "configId": "ACM-1001",
        "aircraftType": "A320",
        "softwareVersion": "1.2.3",
        "navDataCycle": "AIRAC-2024-08",
        "modules": [{"name": "FMS", "enabled": True}]
    }

    res = await client.post("/analyze", json=payload)
    body = res.json()

    assert res.status_code == 200
    assert body["errors"] == []
    assert body["warnings"] == []


@pytest.mark.anyio
async def test_analyze_flags_bad_inputs(client):
    payload = {
        "softwareVersion": "1.2",  # not semver
        "navDataCycle": "2024-08",  # not AIRAC
        "modules": [{"name": "Weather", "enabled": False}]
    }

    res = await client.post("/analyze", json=payload)
    body = res.json()

    assert res.status_code == 200
    assert "softwareVersion missing or not semver" in body["errors"]
    assert "navDataCycle should match AIRAC-YYYY-NN" in body["warnings"]
    assert "FMS module not enabled" in body["warnings"]
