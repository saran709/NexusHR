# NexusHR Render Deployment & Database Configuration Guide

This guide details how NexusHR connects to the Render PostgreSQL database instance using the provided credentials.

---

## 1. Render Database Connection Details
- **Hostname**: `dpg-dascf3npn0mc73fqhv30-a`
- **Port**: `5432`
- **Database Name**: `nexushr_mv19`
- **Username**: `nexushr_mv19_user`
- **Password**: `yQyP0f33EQJKRgCpAw0rKGgisSg0oQfN`
- **Internal Database URL**: `postgresql://nexushr_mv19_user:yQyP0f33EQJKRgCpAw0rKGgisSg0oQfN@dpg-dascf3npn0mc73fqhv30-a:5432/nexushr_mv19`
- **External Database URL**: `postgresql://nexushr_mv19_user:yQyP0f33EQJKRgCpAw0rKGgisSg0oQfN@dpg-dascf3npn0mc73fqhv30-a:5432/nexushr_mv19`

---

## 2. Environment Variables for Render Services
Configure the following environment variables in your Render service dashboards (`Environment` tab):

| Environment Variable | Value |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `production` |
| `DB_URL` | `jdbc:postgresql://dpg-dascf3npn0mc73fqhv30-a:5432/nexushr_mv19` |
| `DB_USER` | `nexushr_mv19_user` |
| `DB_PASSWORD` | `yQyP0f33EQJKRgCpAw0rKGgisSg0oQfN` |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://dpg-dascf3npn0mc73fqhv30-a:5432/nexushr_mv19` |
| `SPRING_DATASOURCE_USERNAME` | `nexushr_mv19_user` |
| `SPRING_DATASOURCE_PASSWORD` | `yQyP0f33EQJKRgCpAw0rKGgisSg0oQfN` |
| `JWT_SECRET` | `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970` |

---

## 3. PSQL Connection Command
To connect to the database via `psql`:
```bash
PGPASSWORD='yQyP0f33EQJKRgCpAw0rKGgisSg0oQfN' psql -h dpg-dascf3npn0mc73fqhv30-a -U nexushr_mv19_user -d nexushr_mv19 -p 5432
```
