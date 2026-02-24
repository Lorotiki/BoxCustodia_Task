#!/usr/bin/env python3
"""
Mock Interview Simulator - Preguntero Técnico TaskFlow
Simula una entrevista técnica con preguntas aleatorias
"""

import random
import re
from typing import List, Dict, Tuple

# Diccionario de preguntas y respuestas
QUESTIONS_DB = {
    "Teóricas Simples": {
        "¿Qué es una API REST?": "Una API REST expone recursos a través de HTTP usando métodos como GET, POST, PUT, PATCH y DELETE, y responde con formatos como JSON. Sigue el modelo cliente-servidor y es stateless.",
        "¿Qué hace Spring Boot?": "Simplifica la creación de aplicaciones Spring al autoconfigurar componentes y permitir arrancar con poca configuración. Incluye servidor embebido y facilita el desarrollo rápido.",
        "¿Qué es JPA?": "Una especificación para mapear objetos Java a tablas de base de datos y manejar persistencia con anotaciones. Define un estándar para ORM.",
        "¿Qué es Hibernate?": "Es la implementación más común de JPA que gestiona el mapeo ORM y las operaciones SQL automáticamente. Abstrae los detalles de la BD.",
        "¿Qué significa ORM?": "Object-Relational Mapping: mapeo entre clases Java y tablas SQL. Permite trabajar con objetos en lugar de escribir SQL directamente.",
        "¿Qué es una entidad en JPA?": "Una clase anotada con @Entity que representa una tabla de base de datos. Está gestionada por el persistence context de JPA.",
        "¿Qué hace @Id y @GeneratedValue?": "@Id marca la clave primaria de la entidad; @GeneratedValue indica que se auto-genera (por ejemplo, con autoincremental).",
        "¿Qué diferencia hay entre PUT y PATCH?": "PUT reemplaza el recurso completo; PATCH actualiza parcialmente uno o más campos. PUT requiere enviar todo; PATCH solo lo que cambia.",
        "¿Qué es un DTO y para qué sirve?": "Un DTO (Data Transfer Object) es un objeto para transportar datos entre capas o cliente-servidor, evitando exponer entidades directamente.",
        "¿Qué es BCrypt y por qué se usa?": "Un algoritmo de hashing para contraseñas, seguro porque incluye salt y es lento a propósito, dificultando ataques de fuerza bruta.",
    },
    "Teóricas Medias": {
        "¿Qué es Inversión de Control (IoC) en Spring?": "Es el principio donde el framework crea y gestiona objetos (beans) y los inyecta donde se necesitan, en lugar de que el código maneje instancias.",
        "¿Qué es la Inyección de Dependencias (DI)?": "Es el mecanismo de pasar dependencias a una clase en lugar de que la clase las cree por sí misma, mejorando desacoplamiento y testabilidad.",
        "¿Qué es un Bean en Spring?": "Un objeto gestionado por el contenedor de Spring, creado y configurado por él. Se registra con anotaciones como @Component, @Service.",
        "¿Qué diferencia hay entre @Component, @Service y @Repository?": "Todas registran beans, pero @Service y @Repository agregan semántica específica (lógica de negocio y acceso a datos) y manejo especial de excepciones.",
        "¿Qué es una transacción en JPA?": "Un conjunto de operaciones que se ejecutan como una unidad atómica: o se completan todas o ninguna. Garantiza consistencia.",
        "¿Para qué sirve @Transactional?": "Marca un método/clase para que Spring gestione transacciones automáticamente, abriendo, commiteando y rollbackeando según corresponda.",
        "¿Qué es la paginación en Spring Data?": "Es la forma de obtener resultados en páginas usando Pageable y Page<T>. Mejora performance y UX al dividir grandes datasets.",
        "¿Qué es un Pageable y un Page<T>?": "Pageable define la página solicitada, tamaño y orden; Page<T> contiene los resultados y metadatos (total de elementos, páginas).",
        "¿Qué es una excepción controlada vs no controlada?": "Controlada (checked): heredan de Exception y deben manejarse o declararse. No controlada: RuntimeException, se propagan automáticamente.",
        "¿Qué es una capa de servicio y por qué es importante?": "Contiene la lógica de negocio y coordina repositorios. Separa controllers de repositorios, mejorando mantenibilidad y testabilidad.",
    },
    "Diseño Simples": {
        "¿Qué es separación de capas y por qué se usa?": "Dividir la aplicación en controller, service y repository. Mejora mantenibilidad, testabilidad y permite cambios independientes en cada capa.",
        "¿Qué responsabilidad tiene el Controller?": "Recibir HTTP, mapear requests a DTOs, validación básica y delegar lógica al service. No debe contener lógica de negocio.",
        "¿Qué responsabilidad tiene el Service?": "Contener la lógica de negocio, coordinar repositorios, realizar validaciones y coordinar transacciones.",
        "¿Qué responsabilidad tiene el Repository?": "Acceder a la base de datos y encapsular consultas. Abstrae detalles de JPA y SQL del resto de la aplicación.",
        "¿Por qué usar DTOs en vez de entidades?": "Para controlar el contrato de la API y evitar exponer campos internos o relaciones que no deben ser públicas.",
        "¿Qué es un contrato de API?": "El formato y reglas de request/response que el cliente espera. Debe ser estable y versionable.",
        "¿Qué es un endpoint REST?": "Una URL que representa un recurso y permite operar sobre él (GET, POST, PUT, PATCH, DELETE).",
        "¿Por qué es importante usar códigos HTTP correctos?": "Mejora la semántica, facilita integración con clientes y herramientas de debugging. Permite diferenciar entre 200, 201, 204, 400, 404, 409, 500.",
        "¿Qué significa resource-oriented design?": "Diseñar la API alrededor de recursos (/tasks, /users) y sus acciones, no alrededor de verbos de acciones.",
        "¿Qué es un CRUD?": "Create, Read, Update, Delete: cuatro operaciones básicas sobre un recurso, correspondientes a POST, GET, PUT/PATCH, DELETE.",
    },
    "Técnicas Simples": {
        "¿Qué hace @RestController?": "Marca la clase como controller REST y retorna JSON por defecto (implica @Controller + @ResponseBody).",
        "¿Qué diferencia hay entre @RequestParam y @PathVariable?": "@RequestParam toma parámetros de query (?status=DONE); @PathVariable toma valores de la URL (/tasks/123).",
        "¿Qué hace @RequestBody?": "Convierte el JSON del request en un objeto Java automáticamente (deserialización).",
        "¿Qué hace @Valid?": "Ejecuta validaciones definidas en el DTO (anotaciones como @NotNull, @Email, etc.).",
        "¿Qué es Lombok y qué hace @Data?": "Lombok es una librería que genera código boilerplate. @Data genera getters, setters, equals, hashCode, toString.",
        "¿Para qué sirve ResponseEntity?": "Permite definir el cuerpo y el status HTTP explícitamente en la respuesta.",
        "¿Qué hace @GetMapping?": "Mapea un endpoint HTTP GET a un método del controller. Shortcut para @RequestMapping(method = RequestMethod.GET).",
        "¿Qué hace @Autowired?": "Inyecta automáticamente una dependencia desde el contenedor de Spring.",
        "¿Qué es un PageRequest?": "Un objeto que define página, tamaño y orden para paginación. Se pasa como Pageable al repository.",
        "¿Qué devuelve un método del repository que retorna Optional?": "Un contenedor que puede tener valor o estar vacío. Evita null y obliga a manejar el caso 'no encontrado'.",
    },
    "Cuestiones de Código Simples": {
        "¿Por qué @RequestBody en el login?": "Porque los datos (email, password) vienen en JSON en el body y deben mapearse al DTO LoginRequest.",
        "¿Por qué usar Optional al buscar por ID?": "Evita NullPointerException y obliga a manejar explícitamente el caso 'no encontrado' con .orElseThrow() o .ifPresent().",
        "¿Por qué ResponseEntity en los controllers?": "Permite devolver status HTTP correctos (201, 204, 404) junto con el cuerpo, en lugar de solo retornar el objeto.",
        "¿Qué pasa si no uso @Valid en un DTO?": "Las validaciones anotadas (@NotNull, @Email) no se ejecutan y entran datos inválidos sin error.",
        "¿Por qué separar CreateTaskRequest de UpdateTaskRequest?": "Porque los campos requeridos pueden ser distintos. En creación, status es opcional; en actualización, puede serlo también.",
        "¿Por qué @Enumerated(EnumType.STRING) en status?": "Evita errores cuando cambias el orden de los enums o agregas valores nuevos en el medio.",
        "¿Qué hace @JsonFormat en fechas?": "Define el formato de serialización/deserialización. Ejemplo: @JsonFormat(pattern = 'yyyy-MM-dd') para que cliente envíe '2025-03-15'.",
        "¿Por qué @Column(unique = true) en email?": "Evita duplicados a nivel BD y agrega una segunda línea de defensa además de la validación lógica en el service.",
        "¿Por qué devolver 201 en POST?": "Porque se creó un recurso nuevo. Es más semánticamente correcta que 200 y comunica al cliente que hubo creación.",
        "¿Qué ocurre si no manejo ResourceNotFoundException?": "El cliente recibe un 500 Internal Server Error genérico en lugar de 404, confundiendo si fue error del servidor o dato no encontrado.",
    }
}

def get_level_questions(difficulty: str) -> Dict[str, str]:
    """Obtiene preguntas de un nivel específico"""
    category = f"{difficulty.title()} {difficulty.split()[-1]}"
    return QUESTIONS_DB.get(category, {})

def format_question(question: str, number: int) -> str:
    """Formatea una pregunta para presentación"""
    return f"\n{'='*70}\n🎯 PREGUNTA #{number}\n{'='*70}\n\n{question}"

def format_answer(answer: str, show: bool = False) -> str:
    """Formatea una respuesta"""
    if show:
        return f"\n💡 RESPUESTA:\n{'-'*70}\n{answer}\n{'-'*70}"
    else:
        return "\n[Presiona Enter para ver la respuesta...]"

def run_mock_interview():
    """Ejecuta una entrevista simulada"""
    
    print("\n" + "="*70)
    print("🚀 MOCK INTERVIEW - PREGUNTERO TÉCNICO TASKFLOW")
    print("="*70)
    print("\nBienvenido a tu simulación de entrevista técnica.")
    print("Responde las preguntas y luego verifica tu respuesta.\n")
    
    # Seleccionar dificultad
    print("Selecciona dificultad:")
    print("1. Teóricas Simples")
    print("2. Teóricas Medias")
    print("3. Diseño Simples")
    print("4. Técnicas Simples")
    print("5. Cuestiones Código Simples")
    
    choice = input("\nOpción (1-5): ").strip()
    
    category_map = {
        "1": "Teóricas Simples",
        "2": "Teóricas Medias",
        "3": "Diseño Simples",
        "4": "Técnicas Simples",
        "5": "Cuestiones de Código Simples"
    }
    
    category = category_map.get(choice, "Teóricas Simples")
    
    if category not in QUESTIONS_DB:
        print(f"❌ Categoría no encontrada: {category}")
        return
    
    questions_dict = QUESTIONS_DB[category]
    questions_list = list(questions_dict.items())
    
    # Seleccionar cantidad
    num_questions = input(f"\n¿Cuántas preguntas? (1-{len(questions_list)}): ").strip()
    try:
        num_questions = min(int(num_questions), len(questions_list))
    except:
        num_questions = 5
    
    # Preguntas aleatorias
    selected = random.sample(questions_list, num_questions)
    
    score = 0
    answered = 0
    
    for i, (question, answer) in enumerate(selected, 1):
        print(format_question(question, i))
        
        # Pausa para que el usuario responda
        input(format_answer(answer, False))
        
        # Mostrar respuesta
        print(format_answer(answer, True))
        
        # Evaluar
        feedback = input("\n✓ Tu respuesta fue similar a esta? (s/n): ").strip().lower()
        if feedback == 's':
            score += 1
            answered += 1
            print("✅ Bien!")
        else:
            answered += 1
            print("⚠️ Revisaré más esta pregunta...")
        
        if i < num_questions:
            input("\n[Presiona Enter para la siguiente pregunta...]")
    
    # Resultado
    percentage = (score / answered * 100) if answered > 0 else 0
    print("\n" + "="*70)
    print("📊 RESULTADO DE LA ENTREVISTA")
    print("="*70)
    print(f"Preguntas respondidas: {answered}/{num_questions}")
    print(f"Respuestas similares: {score}/{answered}")
    print(f"Porcentaje: {percentage:.1f}%")
    
    if percentage >= 80:
        print("🎉 ¡Excelente! Estás bien preparado.")
    elif percentage >= 60:
        print("👍 Bueno, pero sigue practicando.")
    else:
        print("📚 Necesitas repasar más. ¡Sigue estudiando!")
    
    print("="*70 + "\n")

if __name__ == '__main__':
    try:
        run_mock_interview()
    except KeyboardInterrupt:
        print("\n\n👋 ¡Hasta luego!")
    except Exception as e:
        print(f"\n❌ Error: {e}")
