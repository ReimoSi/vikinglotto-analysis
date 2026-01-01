# -*- coding: utf-8 -*-
import csv
import sys
from pathlib import Path

TABLE = "draws"
COLS  = ["draw_id", "draw_date", "main_numbers", "bonus_numbers"]

def q(v: str) -> str:
    if v is None: return "NULL"
    v = str(v).strip().replace("'", "''")
    return f"'{v}'"

def row_to_sql(row: dict) -> str:
    return f"INSERT INTO {TABLE} ({', '.join(COLS)}) VALUES ({q(row['draw_id'])}, {q(row['draw_date'])}, {q(row['main_numbers'])}, {q(row['bonus_numbers'])});"

def convert(csv_path: Path, out_sql: Path):
    with csv_path.open("r", encoding="utf-8") as f, out_sql.open("w", encoding="utf-8", newline="") as out:
        r = csv.DictReader(f)
        need = set(COLS)
        got  = set([c.strip() for c in (r.fieldnames or [])])
        if not need.issubset(got):
            raise SystemExit(f"CSV headers must include: {COLS}; got: {list(got)}")
        out.write("-- generated from CSV\n")
        cnt = 0
        for row in r:
            out.write(row_to_sql(row) + "\n")
            cnt += 1
    print(f"OK: wrote {cnt} INSERTs to {out_sql}")

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python csv_to_data_sql.py <input.csv> <output.sql>")
        sys.exit(1)
    convert(Path(sys.argv[1]), Path(sys.argv[2]))
