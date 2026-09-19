from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
import pandas as pd
import joblib
import xgboost as xgb
import json
from pathlib import Path


# ============================================================
# CONFIGURACIÓN
# ============================================================

app = FastAPI(
    title="HelpWithTEACCH - Servicio de IA",
    description="Servicio de predicción de riesgo de ASD",
    version="1.0.0"
)


# ============================================================
# RUTAS DE LOS MODELOS
# ============================================================

BASE_DIR = Path(__file__).resolve().parent
MODELOS_DIR = BASE_DIR / "modelos"


# ============================================================
# CARGAR MODELOS
# ============================================================

try:
    # --------------------------------------------------------
    # RANDOM FOREST
    # --------------------------------------------------------

    rf_model = joblib.load(
        MODELOS_DIR / "modelo_random_forest.pkl"
    )


    # --------------------------------------------------------
    # XGBOOST
    # --------------------------------------------------------

    xgb_model = xgb.XGBClassifier()

    xgb_model.load_model(
        MODELOS_DIR / "modelo_xgboost.json"
    )


    # --------------------------------------------------------
    # SCALER
    # --------------------------------------------------------

    scaler = joblib.load(
        MODELOS_DIR / "scaler.pkl"
    )


    # --------------------------------------------------------
    # COLUMNAS DEL MODELO
    # --------------------------------------------------------

    with open(
            MODELOS_DIR / "columnas.json",
            "r",
            encoding="utf-8"
    ) as archivo:

        columnas_modelo = json.load(archivo)


except Exception as e:

    raise RuntimeError(
        f"No se pudieron cargar los modelos: {e}"
    )


# ============================================================
# DTO DE ENTRADA
# ============================================================

class PrediccionRequest(BaseModel):

    A1: int = Field(..., ge=0, le=1)
    A2: int = Field(..., ge=0, le=1)
    A3: int = Field(..., ge=0, le=1)
    A4: int = Field(..., ge=0, le=1)
    A5: int = Field(..., ge=0, le=1)

    A6: int = Field(..., ge=0, le=1)
    A7: int = Field(..., ge=0, le=1)
    A8: int = Field(..., ge=0, le=1)
    A9: int = Field(..., ge=0, le=1)
    A10: int = Field(..., ge=0, le=1)

    edad: float = Field(..., ge=0)

    sexo: int

    jaundice: int = Field(..., ge=0, le=1)

    familia_asd: int = Field(..., ge=0, le=1)

    etnia: str

    quien_completo: str


# ============================================================
# ENDPOINT PRINCIPAL
# ============================================================

@app.post("/api/v1/predicciones")
def predecir(request: PrediccionRequest):

    try:

        # ----------------------------------------------------
        # 1. Q-CHAT SCORE
        # ----------------------------------------------------

        qchat_score = sum([
            request.A1,
            request.A2,
            request.A3,
            request.A4,
            request.A5,
            request.A6,
            request.A7,
            request.A8,
            request.A9,
            request.A10
        ])


        # ----------------------------------------------------
        # 2. CREAR REGISTRO ORIGINAL
        # ----------------------------------------------------

        nuevo = pd.DataFrame([{

            "A1": request.A1,
            "A2": request.A2,
            "A3": request.A3,
            "A4": request.A4,
            "A5": request.A5,

            "A6": request.A6,
            "A7": request.A7,
            "A8": request.A8,
            "A9": request.A9,

            "A10_Autism_Spectrum_Quotient":
                request.A10,

            "Age_Years":
                request.edad,

            "Qchat_10_Score":
                qchat_score,

            "Sex":
                request.sexo,

            "Jaundice":
                request.jaundice,

            "Family_mem_with_ASD":
                request.familia_asd,

            "Ethnicity":
                request.etnia,

            "Who_completed_the_test":
                request.quien_completo

        }])


        # ----------------------------------------------------
        # 3. ONE-HOT ENCODING
        # ----------------------------------------------------

        nuevo = pd.get_dummies(
            nuevo,
            columns=[
                "Ethnicity",
                "Who_completed_the_test"
            ]
        )


        # ----------------------------------------------------
        # 4. MISMAS COLUMNAS DEL MODELO
        # ----------------------------------------------------

        nuevo = nuevo.reindex(
            columns=columnas_modelo,
            fill_value=False
        )


        # ----------------------------------------------------
        # 5. STANDARD SCALER
        # ----------------------------------------------------

        nuevo_scaled = scaler.transform(
            nuevo
        )


        # ----------------------------------------------------
        # 6. RANDOM FOREST
        # ----------------------------------------------------

        rf_pred = rf_model.predict(
            nuevo_scaled
        )[0]

        rf_prob = rf_model.predict_proba(
            nuevo_scaled
        )[0][1]


        # ----------------------------------------------------
        # 7. XGBOOST
        # ----------------------------------------------------

        xgb_pred = xgb_model.predict(
            nuevo_scaled
        )[0]

        xgb_prob = xgb_model.predict_proba(
            nuevo_scaled
        )[0][1]


        # ----------------------------------------------------
        # 8. RESPUESTA
        # ----------------------------------------------------

        return {

            "qchat_score": qchat_score,

            "random_forest": {

                "prediction":
                    int(rf_pred),

                "resultado": (
                    "ASD"
                    if rf_pred == 1
                    else "No ASD"
                ),

                "probabilidad_asd":
                    float(rf_prob)
            },

            "xgboost": {

                "prediction":
                    int(xgb_pred),

                "resultado": (
                    "ASD"
                    if xgb_pred == 1
                    else "No ASD"
                ),

                "probabilidad_asd":
                    float(xgb_prob)
            }
        }


    except Exception as e:

        raise HTTPException(
            status_code=500,
            detail=f"Error durante la predicción: {str(e)}"
        )


# ============================================================
# ENDPOINT DE PRUEBA
# ============================================================

@app.get("/")
def inicio():

    return {
        "servicio": "HelpWithTEACCH - IA",
        "estado": "activo"
    }