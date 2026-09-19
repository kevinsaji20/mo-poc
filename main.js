const crypto = require("crypto");

async function sendEngagementEvent() {
    const accessToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIyOWJjNmRiNS0yNGVlLTQ5ZTAtOTgzNi1hYWRkZDNkNGRkMGIiLCJlbWFpbCI6Imtldmluc2FqaTIwQGdtYWlsLmNvbSIsInJvbGVzIjpbIkFOQUxZVElDU19SRUFEIiwiRVZFTlRfSU5HRVNUIiwiQU5BTFlUSUNTX0FETUlOIiwiQ09OVEVOVF9BRE1JTiJdLCJqdGkiOiI2NTA4NGQ2My0yZjA5LTQ0NTgtYTMxNy0zMTExYmZjYWUxYzciLCJpYXQiOjE3ODk3ODY2MzUsImV4cCI6MTc4OTc5MDIzNX0.EimUSNVwdssS5e16T3vOj5gbxYDo6dPPLnBVviW4QdM1Rm9KXRdV3vkv-NvJMdZLpvFoXnXt3T7ZDrt43wC1lUVE5NR_rF69diIsakiYh34ARXq2AGqRZcXybf9XMoog47iqJUiRtqxU5M2ZVDc_I31s8fo0iE5_pMFFiExDFnYAGCcMhe-Gmp2zUpZlco-Z8-9horXnJ5KebRxcFb4nPoakG9LZYmlcZg1ydvshSPFJvqDfmqSbt6Pkxfz02rLVOFylNGWam1RhkXpThYaqXcKhHN2qqP13UxaKC-hZRnv8s3qYXFYU2FBZPiz8oT7liJix8K_dLL2Lob0NFfo8bA"
    const url = "http://localhost:8080/api/v1/events"; // change this

    const payload = {
        eventId: crypto.randomUUID(),
        contentId: "1551b3c5-f0db-4f5e-9a5d-60a73477de4d",
        userId: "29bc6db5-24ee-49e0-9836-aaddd3d4dd0b",
        eventType: "PLAY",
        playbackPositionMs: 0,
        totalDurationMs: 3600000,
        eventTimeStamp: new Date().toISOString(),
        sessionId: "8f7c3c4e-6c8f-4b31-a2a9-7b3f7c8e1d92",
        deviceType: "DESKTOP",
        region: "IN",
        seekFromPosition: null,
        seekToPosition: null
    };

    const response = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${accessToken}`
        },
        body: JSON.stringify(payload)
    });

    console.log("Status:", response.status);
    console.log("Response:", await response.text());
}

sendEngagementEvent();