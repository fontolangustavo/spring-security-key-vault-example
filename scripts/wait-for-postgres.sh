#!/bin/bash
set -e

check_products_table() {
  PGPASSWORD=MyP455 psql -h postgres -U products_db_user -d products_db -c "SELECT COUNT(*) FROM products;" | grep -q '^[0-9]*$'
}

until pg_isready -h postgres -U products_db_user -d products_db; do
  echo "Aguardando o PostgreSQL estar pronto..."
  sleep 2
done

echo "PostgreSQL está pronto!"

until check_products_table; do
  echo "Aguardando a tabela 'products' ser criada e conter itens..."
  sleep 2
done

echo "A tabela 'products' existe e contém itens!"
exec "$@"
