from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Dict, Any
import re

app = FastAPI(title="ACM Analyzer", version="0.1.0")


class ConfigPayload(BaseModel):
    __root__: Dict[str, Any]

    @property
    def data(self) -> Dict[str, Any]:
        return self.__root__


def check_airac(value: str) -> bool:
    return bool(re.match(r"^AIRAC-\\d{4}-\\d{2}$", value or ""))


def check_semver(value: str) -> bool:
    return bool(re.match(r"^\\d+\\.\\d+\\.\\d+$", value or ""))


@app.post("/analyze")
def analyze(payload: ConfigPayload):
    data = payload.data
    warnings: List[str] = []
    errors: List[str] = []

    nav_cycle = str(data.get("navDataCycle", ""))
    if not check_airac(nav_cycle):
        warnings.append("navDataCycle should match AIRAC-YYYY-NN")

    version = str(data.get("softwareVersion", ""))
    if not check_semver(version):
        errors.append("softwareVersion missing or not semver")

    modules = data.get("modules") or []
    if not isinstance(modules, list):
        errors.append("modules must be a list")
    else:
        fms_enabled = any(
            (m.get("name", "").upper() == "FMS" and m.get("enabled", False))
            for m in modules
            if isinstance(m, dict)
        )
        if not fms_enabled:
            warnings.append("FMS module not enabled")
    return {"warnings": warnings, "errors": errors}


@app.get("/health")
def health():
    return {"status": "ok"}
