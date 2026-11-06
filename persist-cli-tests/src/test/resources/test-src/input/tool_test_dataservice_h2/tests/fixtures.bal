// H2 stores a time:TimeOfDay in a TIME column without fractional seconds, so the
// document that the ISO 8601 and base64 round trip uses.
final json document = {
    code: "D1",
    issuedOn: "2024-01-02",
    expiresOn: (),
    opensAt: "10:15:30",
    createdAt: "2024-01-02T10:15:30",
    signedAt: "2024-01-02T10:15:30Z",
    content: "AAEC+v8="
};
