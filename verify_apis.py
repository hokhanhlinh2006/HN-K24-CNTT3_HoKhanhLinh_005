import urllib.request
import json
import sys

BASE_URL = "http://localhost:8081/api/v1"

def make_request(url, method="GET", headers=None, body=None):
    if headers is None:
        headers = {}
    if body is not None:
        data = json.dumps(body).encode("utf-8")
        headers["Content-Type"] = "application/json"
    else:
        data = None
        
    req = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req) as response:
            res_body = response.read().decode("utf-8")
            return response.status, json.loads(res_body) if res_body else None
    except urllib.error.HTTPError as e:
        res_body = e.read().decode("utf-8")
        try:
            return e.code, json.loads(res_body)
        except Exception:
            return e.code, res_body
    except Exception as e:
        print(f"Connection error: {e}")
        sys.exit(1)

def main():
    print("==================================================")
    print("STARTING API VERIFICATION SCENARIOS")
    print("==================================================")
    
    # 1. Login Student
    print("\n[SCENARIO 1] Logging in as Student...")
    status, res = make_request(f"{BASE_URL}/auth/login", "POST", body={
        "email": "student@gmail.com",
        "password": "123456789"
    })
    print(f"Status: {status}")
    student_token = res["data"]["token"]
    print(f"Student Token retrieved successfully.")
    
    # 2. Login Admin
    print("\n[SCENARIO 2] Logging in as Admin...")
    status, res = make_request(f"{BASE_URL}/auth/login", "POST", body={
        "email": "admin@gmail.com",
        "password": "123456789"
    })
    print(f"Status: {status}")
    admin_token = res["data"]["token"]
    print(f"Admin Token retrieved successfully.")

    student_headers = {"Authorization": f"Bearer {student_token}"}
    admin_headers = {"Authorization": f"Bearer {admin_token}"}
    
    # 3. Public Lookup: Valid Certificate
    print("\n[SCENARIO 3] Public lookup for Valid Certificate 'CERT-VALID-001'...")
    status, res = make_request(f"{BASE_URL}/certificates/lookup/CERT-VALID-001")
    print(f"Status: {status}")
    print(f"Response Message: {res['message']}")
    print(f"Certificate Code: {res['data']['certificateCode']}")
    print(f"Student: {res['data']['studentName']}")
    print(f"Course: {res['data']['courseTitle']}")
    print(f"Status: {res['data']['status']}")
    assert status == 200
    assert res['data']['status'] == "Hợp lệ"

    # 4. Public Lookup: Expired Certificate
    print("\n[SCENARIO 4] Public lookup for Expired Certificate 'CERT-EXPIRED-002'...")
    status, res = make_request(f"{BASE_URL}/certificates/lookup/CERT-EXPIRED-002")
    print(f"Status: {status}")
    print(f"Response Message: {res['message']}")
    assert status == 410
    assert res['message'] == "Đã hết hạn"

    # 5. Public Lookup: Revoked Certificate (Cheated)
    print("\n[SCENARIO 5] Public lookup for Revoked Certificate 'CERT-REVOKED-003'...")
    status, res = make_request(f"{BASE_URL}/certificates/lookup/CERT-REVOKED-003")
    print(f"Status: {status}")
    print(f"Response Message: {res['message']}")
    assert status == 400
    assert res['message'] == "Chứng chỉ không hợp lệ do vi phạm!"

    # 6. Public Lookup: Non-existing Certificate
    print("\n[SCENARIO 6] Public lookup for Non-existent Certificate 'CERT-NON-EXIST'...")
    status, res = make_request(f"{BASE_URL}/certificates/lookup/CERT-NON-EXIST")
    print(f"Status: {status}")
    print(f"Response Message: {res['message']}")
    assert status == 404
    assert res['message'] == "Không tìm thấy chứng chỉ với mã đã nhập."

    # 7. Student API: Get my certificates
    print("\n[SCENARIO 7] Fetching Student's own certificates...")
    status, res = make_request(f"{BASE_URL}/certificates/my", headers=student_headers)
    print(f"Status: {status}")
    print(f"Certificates Count: {len(res['data'])}")
    for cert in res['data']:
        print(f" - Code: {cert['certificateCode']} | Course: {cert['courseTitle']} | Status: {cert['status']}")
    assert status == 200

    # 8. Admin API: Issue new certificate
    print("\n[SCENARIO 8] Admin issues new certificate for Student...")
    status, res = make_request(f"{BASE_URL}/admin/certificates/issue", "POST", headers=admin_headers, body={
        "userId": 2, # Nguyen Van Student id
        "courseId": 1
    })
    print(f"Status: {status}")
    new_code = res['data']['certificateCode']
    print(f"Issued Certificate Code: {new_code}")
    assert status == 200

    # 9. Verify lookup for newly issued certificate
    print("\n[SCENARIO 9] Lookup newly issued certificate...")
    status, res = make_request(f"{BASE_URL}/certificates/lookup/{new_code}")
    print(f"Status: {status}")
    print(f"Status in data: {res['data']['status']}")
    assert status == 200
    assert res['data']['status'] == "Hợp lệ"

    # 10. Admin API: Revoke the newly issued certificate
    print(f"\n[SCENARIO 10] Admin revoking newly issued certificate {new_code} due to fraud...")
    status, res = make_request(f"{BASE_URL}/admin/certificates/{new_code}/revoke", "POST", headers=admin_headers, body={
        "reason": "Học viên sao chép bài làm của bạn khác"
    })
    print(f"Status: {status}")
    print(f"Revoked reason saved: {res['data']['revokedReason']}")
    assert status == 200

    # 11. Verify lookup again for revoked certificate
    print("\n[SCENARIO 11] Lookup revoked certificate again...")
    status, res = make_request(f"{BASE_URL}/certificates/lookup/{new_code}")
    print(f"Status: {status}")
    print(f"Response Message: {res['message']}")
    assert status == 400
    assert res['message'] == "Chứng chỉ không hợp lệ do vi phạm!"

    # 12. Security Check: Accessing Admin API with Student role
    print("\n[SCENARIO 12] Security check: Student attempts to issue a certificate...")
    status, res = make_request(f"{BASE_URL}/admin/certificates/issue", "POST", headers=student_headers, body={
        "userId": 2,
        "courseId": 2
    })
    print(f"Status: {status}")
    print(f"Response: {res}")
    # 403 Forbidden is expected
    assert status == 403

    # 13. Security Check: Accessing Student API without token
    print("\n[SCENARIO 13] Security check: Unauthenticated access to My Certificates...")
    status, res = make_request(f"{BASE_URL}/certificates/my")
    print(f"Status: {status}")
    print(f"Response: {res}")
    # 401 Unauthorized is expected
    assert status == 401

    print("\n==================================================")
    print("ALL API VERIFICATION SCENARIOS PASSED SUCCESSFULLY!")
    print("==================================================")

if __name__ == "__main__":
    main()
