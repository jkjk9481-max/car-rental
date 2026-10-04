// 목록 → 상세 → 예약 화면에서 같은 날짜 형식과 전달 규칙을 사용하기 위한 공통 함수다.
// datetime-local 입력값은 '2026-10-10T09:00'처럼 시간대가 없는 문자열이다.
// 백엔드도 LocalDateTime을 받으므로 UTC 변환(toISOString)은 하지 않는다.
function parseLocalTime(value) {
    if (!/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(value)) return NaN;

    const date = new Date(value);
    // Date는 2월 30일 같은 값을 다음 달로 보정할 수 있어 원래 입력과 비교한다.
    const [year, month, day, hour, minute] = value.split(/[-T:]/).map(Number);
    if (date.getFullYear() !== year || date.getMonth() + 1 !== month
        || date.getDate() !== day || date.getHours() !== hour
        || date.getMinutes() !== minute) return NaN;
    return date.getTime();
}

// 빈 문자열은 검증 통과, 그 외 문자열은 화면에 보여 줄 안내 문구다.
// 프론트 검사는 입력 안내용이다. 실제 예약 가능 여부는 서버가 다시 검사한다.
export function validateReservationTime(startAt, endAt) {
    if (!startAt || !endAt) return "대여 시작 시간과 반납 시간을 모두 입력해 주세요.";
    const start = parseLocalTime(startAt);
    const end = parseLocalTime(endAt);
    if (!Number.isFinite(start) || !Number.isFinite(end)) return "올바른 날짜와 시간을 입력해 주세요.";
    if (start >= end) return "반납 시간은 대여 시작 시간보다 늦어야 합니다.";
    if (start <= Date.now()) return "대여 시작 시간은 현재 시간보다 늦어야 합니다.";
    return "";
}

// URLSearchParams는 :, 공백 같은 문자를 URL에 안전하게 넣고 읽도록 처리한다.
// 반환값의 ?는 주소의 경로와 검색 조건을 구분한다. 시간이 없으면 조건을 붙이지 않는다.
export function createTimeSearch(startAt, endAt) {
    if (!startAt || !endAt) return "";
    return `?${new URLSearchParams({ startAt, endAt }).toString()}`;
}

// URL은 사용자가 직접 수정할 수 있으므로 읽은 값을 그대로 신뢰하지 않는다.
// 잘못된 값은 입력창에 채우지 않고, 원인을 안내한 뒤 새로 입력하게 한다.
export function readTimeSearch(searchParams) {
    const startAt = searchParams.get("startAt") ?? "";
    const endAt = searchParams.get("endAt") ?? "";
    const hasTime = searchParams.has("startAt") || searchParams.has("endAt");
    if (!hasTime) return { startAt: "", endAt: "", message: "" };
    const message = validateReservationTime(startAt, endAt);
    return message ? { startAt: "", endAt: "", message } : { startAt, endAt, message: "" };
}
