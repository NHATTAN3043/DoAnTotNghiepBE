INSERT INTO "CHUCVU" ("maChucVu", "tenChucVu") VALUES (1, 'Back Office');
INSERT INTO "CHUCVU" ("maChucVu", "tenChucVu") VALUES (2, 'Employee');
INSERT INTO "CHUCVU" ("maChucVu", "tenChucVu") VALUES (3, 'Manager');

INSERT INTO "NGUOIDUNG" (
    "ten", "email", "matKhau", "maChucVu"
) VALUES (
             'Nguyen Van A', 'admin@gmail.com','$2a$12$ye0EjxA.tdq38/.xevjeA..m4KVpy.a9NE77oXR.7flLgzed7E7x.', 1
         );

CREATE EXTENSION IF NOT EXISTS unaccent;
