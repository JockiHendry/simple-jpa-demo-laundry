/*
 * Copyright 2014 Jocki Hendry.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package project

import domain.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import simplejpa.testing.DbUnitTestCase

class JenisWorkTest extends DbUnitTestCase {

    private static final Logger log = LoggerFactory.getLogger(JenisWorkTest)

    protected void setUp() {
        super.setUp()
        setUpDatabase("jenisWork", "/project/data.xls")
        controller.destroyEntityManager()
        model.errors.clear()
        model.clear()
    }

    protected void tearDown() {
        super.tearDown()
    }

/*
    public void testListAll() {
        controller.listAll()
        Thread.sleep(3000)
        BasicEventList list = model.studentList
        assertEquals(4, list.size())
        assertTrue(list.contains(new Student("A1", "Class A1")))
        assertTrue(list.contains(new Student("A2", "Class A2")))
        assertTrue(list.contains(new Student("A3", "Class A3")))
        assertTrue(list.contains(new Student("A4", "Class A4")))
    }

    public void testSearch() {
        model.nameSearch = "A3"
        controller.search()
        Thread.sleep(3000)

        BasicEventList list = model.studentList
        assertEquals(1, list.size())
        assertTrue(list.contains(new Student("A3", "Class A3")))
    }

    public void testSave() {
        model.id = null
        model.name = "A5"
        model.location = "Class A5"
        controller.save()
        Thread.sleep(3000)

        List list = controller.findAllStudent()
        assertEquals(5, list.size())
        assertTrue(list.contains(new Student("A5", "Class A5")))
    }

    public void testUpdate() {
        model.id = null
        model.name = "Class A5"
        model.location = "A5"
        controller.save()
        controller.listAll()
        Thread.sleep(3000)

        DefaultEventSelectionModel selection = model.studentSelection
        selection.setSelectionInterval(4,4)
        assertNotNull(selection.selected)
        assertEquals("Class A5", selection.selected[0].name)

        model.name = "Class A6"
        model.location = "A6"

        controller.save()
        Thread.sleep(3000)

        Student student = controller.findStudentByName("Class A6")
        assertEquals("Class A6", student.name)
        assertEquals("A6", student.location)
    }

    public void testDelete() {
        controller.listAll()
        Thread.sleep(3000)
        DefaultEventSelectionModel selection = model.studentSelection
        selection.setSelectionInterval(0,0)
        assertNotNull(selection.selected)
        assertEquals("Class A4", selection.selected[0].name)
        controller.delete()
        Thread.sleep(3000)
        assertNull(controller.findStudentById(-4))
    }
*/

}