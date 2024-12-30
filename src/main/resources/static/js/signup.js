// src/main/resources/static/js/signup.js

document.addEventListener('DOMContentLoaded', function () {
    const maxSelection = 5;
    const checkboxes = document.querySelectorAll('.genre-wrap input[type="checkbox"]');

    checkboxes.forEach(function (checkbox) {
        checkbox.addEventListener('change', function () {
            const checkedCount = document.querySelectorAll('.genre-wrap input[type="checkbox"]:checked').length;
            if (checkedCount >= maxSelection) {
                checkboxes.forEach(function (cb) {
                    if (!cb.checked) {
                        cb.disabled = true;
                        cb.nextElementSibling.style.opacity = '0.6';
                        cb.nextElementSibling.style.cursor = 'not-allowed';
                    }
                });
            } else {
                checkboxes.forEach(function (cb) {
                    cb.disabled = false;
                    cb.nextElementSibling.style.opacity = '1';
                    cb.nextElementSibling.style.cursor = 'pointer';
                });
            }
        });
    });

    // 클라이언트 사이드 유효성 검사
    const form = document.getElementById('signupForm');
    const memEmail = document.getElementById('memEmail');
    const memPw = document.getElementById('memPw');
    const memTel = document.getElementById('memTel');
    const memEmailError = document.getElementById('memEmailError');
    const memEmailDupError = document.getElementById('memEmailDupError');
    const memPwError = document.getElementById('memPwError');
    const memTelError = document.getElementById('memTelError');
    const memPwStrength = document.getElementById('memPwStrength');
    const signupButton = document.getElementById('signupButton');
    const emailCheckButton = document.getElementById('emailCheckButton');
    const genreError = document.getElementById('genreError'); // 추가된 부분

    // 닉네임 관련 변수 추가
    const memNn = document.getElementById('memNn');
    const memNnError = document.getElementById('memNnError');
    const memNnDupError = document.getElementById('memNnDupError');
    const nicknameCheckButton = document.getElementById('nicknameCheckButton');
    let isNicknameUnique = false; // 닉네임 유효성 상태 추적 변수

    // CSRF 토큰과 헤더 이름 가져오기
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // 이메일 유효성 정규식
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    // 전화번호 유효성 정규식: 010 followed by exactly 8 digits
    const phoneRegex = /^010\d{8}$/;

    // 비밀번호 강도 정규식
    const passwordStrengthRegex = {
        letters: /[A-Za-z]/,
        numbers: /\d/,
        special: /[@$!%*?&]/,
    };

    // 유효성 상태 추적 변수
    let isEmailFormatValid = false;
    let isEmailUnique = false;
    let isPasswordValid = false;
    let isPhoneValid = false;
    let isGenreValid = false;

    // 유효성 검사 함수
    function validateForm() {
        // 장르 유효성 상태 업데이트
        const selectedGenres = document.querySelectorAll('.genre-wrap input[type="checkbox"]:checked').length;
        if (selectedGenres >= 1) {
            isGenreValid = true;
            genreError.textContent = '';
        } else {
            isGenreValid = false;
            genreError.textContent = '장르를 하나 이상 선택해주세요.'; // 초기 로드 시에도 오류 메시지 표시
        }

        if (isEmailFormatValid && isEmailUnique && isPasswordValid && isPhoneValid && isGenreValid && isNicknameUnique) {
            signupButton.disabled = false;
            signupButton.classList.add('enabled');
        } else {
            signupButton.disabled = true;
            signupButton.classList.remove('enabled');
        }
    }

    // 이메일 중복 확인 버튼 클릭 시 이벤트
    emailCheckButton.addEventListener('click', function () {
        const email = memEmail.value.trim();

        // 이메일 형식 유효성 검사
        if (!emailRegex.test(email)) {
            memEmailDupError.textContent = '유효한 이메일 주소를 입력해주세요.';
            memEmailDupError.classList.add('invalid');
            memEmailDupError.classList.remove('valid');
            isEmailUnique = false;
            validateForm();
            return;
        }

        // 중복 확인 요청
        fetch('/check_email', { // Ensure the URL is in quotes
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({ email: email })
        })
            .then(response => response.json())
            .then(data => {
                if (data.exists) {
                    memEmailDupError.textContent = '이미 사용 중인 이메일입니다.';
                    memEmailDupError.classList.add('invalid');
                    memEmailDupError.classList.remove('valid');
                    isEmailUnique = false;
                    memEmail.classList.add('invalid');
                    memEmail.classList.remove('valid');
                } else {
                    memEmailDupError.textContent = '사용 가능한 이메일입니다.';
                    memEmailDupError.classList.add('valid');
                    memEmailDupError.classList.remove('invalid');
                    isEmailUnique = true;
                    memEmail.classList.add('valid');
                    memEmail.classList.remove('invalid');
                }
                validateForm();
            })
            .catch(error => {
                console.error('Error checking email duplication:', error);
                memEmailDupError.textContent = '이메일 중복 확인에 실패했습니다. 다시 시도해주세요.';
                memEmailDupError.classList.add('invalid');
                memEmailDupError.classList.remove('valid');
                isEmailUnique = false;
                validateForm();
            });
    });

    // 닉네임 중복 확인 버튼 클릭 시 이벤트
    nicknameCheckButton.addEventListener('click', function () {
        const nickname = memNn.value.trim();

        // 닉네임 형식 유효성 검사 (예: 최소 2자 이상)
        if (nickname.length < 2) {
            memNnDupError.textContent = '닉네임은 최소 2자 이상이어야 합니다.';
            memNnDupError.classList.add('invalid');
            memNnDupError.classList.remove('valid');
            isNicknameUnique = false;
            validateForm();
            return;
        }

        // 중복 확인 요청
        fetch('/check_nickname', { // Ensure the URL is in quotes
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({ nickname: nickname })
        })
            .then(response => response.json())
            .then(data => {
                if (data.exists) {
                    memNnDupError.textContent = '이미 사용 중인 닉네임입니다.';
                    memNnDupError.classList.add('invalid');
                    memNnDupError.classList.remove('valid');
                    isNicknameUnique = false;
                    memNn.classList.add('invalid');
                    memNn.classList.remove('valid');
                } else {
                    memNnDupError.textContent = '사용 가능한 닉네임입니다.';
                    memNnDupError.classList.add('valid');
                    memNnDupError.classList.remove('invalid');
                    isNicknameUnique = true;
                    memNn.classList.add('valid');
                    memNn.classList.remove('invalid');
                }
                validateForm();
            })
            .catch(error => {
                console.error('Error checking nickname duplication:', error);
                memNnDupError.textContent = '닉네임 중복 확인에 실패했습니다. 다시 시도해주세요.';
                memNnDupError.classList.add('invalid');
                memNnDupError.classList.remove('valid');
                isNicknameUnique = false;
                validateForm();
            });
    });

    // 폼 제출 시 유효성 검사
    form.addEventListener('submit', function (e) {
        let valid = true;

        // 이메일 유효성 검사
        if (!emailRegex.test(memEmail.value)) {
            memEmailError.textContent = '유효한 이메일 주소를 입력해주세요.';
            memEmail.classList.add('invalid');
            memEmail.classList.remove('valid');
            isEmailFormatValid = false;
            valid = false;
        } else {
            memEmailError.textContent = '';
            memEmail.classList.add('valid');
            memEmail.classList.remove('invalid');
            isEmailFormatValid = true;
        }

        // 비밀번호 유효성 검사
        const pwd = memPw.value;
        let strengthMessage = '';
        let strengthClass = '';
        let typeCount = 0;

        // 최소 길이 검사
        if (pwd.length < 8) {
            memPwError.textContent = '비밀번호는 최소 8자리를 채워주세요.';
            memPw.classList.add('invalid');
            memPw.classList.remove('valid');
            isPasswordValid = false;
            valid = false;
        } else {
            memPwError.textContent = '';
            // 문자 유형 확인
            if (passwordStrengthRegex.letters.test(pwd)) typeCount++;
            if (passwordStrengthRegex.numbers.test(pwd)) typeCount++;
            if (passwordStrengthRegex.special.test(pwd)) typeCount++;

            if (typeCount >= 2) {
                strengthMessage = typeCount === 2 ? '비밀번호 강도: 보통' : '비밀번호 강도: 강함';
                strengthClass = typeCount === 2 ? 'strength-medium' : 'strength-strong';
                memPw.classList.add('valid');
                memPw.classList.remove('invalid');
                isPasswordValid = true;
            } else {
                strengthMessage = '비밀번호는 영문, 숫자, 특수문자 중 2개 이상을 조합해주세요.';
                strengthClass = 'strength-weak';
                memPw.classList.add('invalid');
                memPw.classList.remove('valid');
                isPasswordValid = false;
                valid = false;
            }

            memPwStrength.innerHTML = `<span class="${strengthClass}">${strengthMessage}</span>`;
        }

        // 전화번호 유효성 검사 (010 followed by 8 digits)
        if (!phoneRegex.test(memTel.value)) {
            memTelError.textContent = '전화번호는 "010"으로 시작하고 8자리 숫자를 입력해주세요. 예: 01012345678';
            memTel.classList.add('invalid');
            memTel.classList.remove('valid');
            isPhoneValid = false;
            valid = false;
        } else {
            memTelError.textContent = '';
            memTel.classList.add('valid');
            memTel.classList.remove('invalid');
            isPhoneValid = true;
        }

        // 닉네임 유효성 검사
        if (!isNicknameUnique) {
            memNnDupError.textContent = '닉네임 중복 확인을 해주세요.';
            memNnDupError.classList.add('invalid');
            memNnDupError.classList.remove('valid');
            isNicknameUnique = false;
            valid = false;
        }

        // 장르 선택 검사 (최소 1개 선택)
        const selectedGenres = document.querySelectorAll('.genre-wrap input[type="checkbox"]:checked').length;
        if (selectedGenres < 1) {
            genreError.textContent = '장르를 하나 이상 선택해주세요.';
            isGenreValid = false;
            valid = false;
        } else {
            genreError.textContent = '';
            isGenreValid = true;
        }

        validateForm();

        if (!valid) {
            e.preventDefault(); // 폼 제출 방지
        }
    });

    // 실시간 유효성 검사
    memEmail.addEventListener('input', function () {
        // 이메일 중복 상태 리셋
        isEmailUnique = false;
        memEmailDupError.textContent = '';
        memEmailDupError.classList.remove('valid', 'invalid');
        validateForm();

        if (emailRegex.test(memEmail.value)) {
            memEmailError.textContent = '';
            memEmail.classList.add('valid');
            memEmail.classList.remove('invalid');
            isEmailFormatValid = true;
        } else {
            memEmailError.textContent = '유효한 이메일 주소를 입력해주세요.';
            memEmail.classList.add('invalid');
            memEmail.classList.remove('valid');
            isEmailFormatValid = false;
        }
        validateForm();
    });

    memPw.addEventListener('input', function () {
        const pwd = memPw.value;
        let strength = '';
        let strengthClass = '';
        let typeCount = 0;

        // 최소 길이 검사
        if (pwd.length < 8) {
            strength = '비밀번호는 최소 8자리를 채워주세요.';
            strengthClass = 'strength-weak';
            memPw.classList.add('invalid');
            memPw.classList.remove('valid');
            isPasswordValid = false;
        } else {
            // 문자 유형 확인
            if (passwordStrengthRegex.letters.test(pwd)) typeCount++;
            if (passwordStrengthRegex.numbers.test(pwd)) typeCount++;
            if (passwordStrengthRegex.special.test(pwd)) typeCount++;

            if (typeCount >= 2) {
                strength = typeCount === 2 ? '비밀번호 강도: 보통' : '비밀번호 강도: 강함';
                strengthClass = typeCount === 2 ? 'strength-medium' : 'strength-strong';
                memPw.classList.add('valid');
                memPw.classList.remove('invalid');
                isPasswordValid = true;
            } else {
                strength = '비밀번호는 영문, 숫자, 특수문자 중 2개 이상을 조합해주세요.';
                strengthClass = 'strength-weak';
                memPw.classList.add('invalid');
                memPw.classList.remove('valid');
                isPasswordValid = false;
            }
        }

        if (pwd.length === 0) {
            memPwStrength.textContent = '';
            memPw.classList.remove('valid', 'invalid');
            isPasswordValid = false;
        } else {
            memPwStrength.innerHTML = `<span class="${strengthClass}">${strength}</span>`;
        }

        // 비밀번호 강도에 따른 오류 메시지 표시
        if (pwd.length < 8) {
            memPwError.textContent = '비밀번호는 최소 8자리를 채워주세요.';
        } else if (typeCount < 2) {
            memPwError.textContent = '비밀번호는 영문, 숫자, 특수문자 중 2개 이상을 조합해주세요.';
        } else {
            memPwError.textContent = '';
        }

        validateForm();
    });

    memTel.addEventListener('input', function () {
        if (phoneRegex.test(memTel.value)) {
            memTelError.textContent = '';
            memTel.classList.add('valid');
            memTel.classList.remove('invalid');
            isPhoneValid = true;
        } else {
            memTelError.textContent = '전화번호는 "010"으로 시작하고 8자리 숫자를 입력해주세요. 예: 01012345678';
            memTel.classList.add('invalid');
            memTel.classList.remove('valid');
            isPhoneValid = false;
        }
        validateForm();
    });

    // 닉네임 실시간 유효성 검사
    memNn.addEventListener('input', function () {
        // 닉네임 중복 상태 리셋
        isNicknameUnique = false;
        memNnDupError.textContent = '';
        memNnDupError.classList.remove('valid', 'invalid');
        validateForm();

        if (memNn.value.trim().length >= 2) {
            memNnError.textContent = '';
            memNn.classList.add('valid');
            memNn.classList.remove('invalid');
        } else {
            memNnError.textContent = '닉네임은 최소 2자 이상이어야 합니다.';
            memNn.classList.add('invalid');
            memNn.classList.remove('valid');
        }
        validateForm();
    });

    // 장르 선택 시 실시간 유효성 검사
    const genreCheckboxes = document.querySelectorAll('.genre-wrap input[type="checkbox"]');
    genreCheckboxes.forEach(function (checkbox) {
        checkbox.addEventListener('change', function () {
            const selectedGenres = document.querySelectorAll('.genre-wrap input[type="checkbox"]:checked').length;
            if (selectedGenres >= 1) {
                genreError.textContent = '';
                isGenreValid = true;
            } else {
                genreError.textContent = '장르를 하나 이상 선택해주세요.';
                isGenreValid = false;
            }
            validateForm();
        });
    });

    // 폼 로드 시 버튼 상태 초기화 및 오류 메시지 표시
    validateForm();
});
