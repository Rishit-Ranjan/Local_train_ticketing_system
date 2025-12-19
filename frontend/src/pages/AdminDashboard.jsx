import React, { useState, useEffect } from 'react';
import { adminService } from '../services/api';
import { Plus, MapPin, Calendar, Train, AlertCircle } from 'lucide-react';
import { motion } from 'framer-motion';

const AdminDashboard = () => {
    const [stations, setStations] = useState([]);
    const [activeTab, setActiveTab] = useState('stations'); // 'stations' or 'schedules'
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    // Station Form State
    const [stationForm, setStationForm] = useState({
        stationCode: '',
        stationName: '',
        city: '',
        state: ''
    });

    // Schedule Form State
    const [scheduleForm, setScheduleForm] = useState({
        trainId: '',
        sourceStationId: '',
        destinationStationId: '',
        departureTime: '',
        arrivalTime: '',
        totalSeats: 100,
        operatingDays: 'DAILY'
    });

    useEffect(() => {
        fetchStations();
    }, []);

    const fetchStations = async () => {
        try {
            const res = await adminService.getAllStations();
            setStations(res.data);
        } catch (err) {
            console.error(err);
        }
    };

    const handleCreateStation = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setMessage('');
        try {
            await adminService.createStation(stationForm);
            setMessage('Station created successfully!');
            setStationForm({ stationCode: '', stationName: '', city: '', state: '' });
            fetchStations();
        } catch (err) {
            setError('Failed to create station.');
        } finally {
            setLoading(false);
        }
    };

    const handleCreateSchedule = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setMessage('');
        try {
            await adminService.createSchedule(scheduleForm);
            setMessage('Schedule created successfully!');
            setScheduleForm({
                trainId: '', sourceStationId: '', destinationStationId: '',
                departureTime: '', arrivalTime: '', totalSeats: 100, operatingDays: 'DAILY'
            });
        } catch (err) {
            setError('Failed to create schedule. Ensure Train ID is valid.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-6xl mx-auto space-y-8 pb-12">
            <header>
                <h1 className="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
                <p className="text-gray-600">Manage trains, stations, and schedules</p>
            </header>

            {/* Tabs */}
            <div className="flex space-x-4 border-b border-gray-200">
                <button
                    onClick={() => setActiveTab('stations')}
                    className={`pb-3 px-4 text-sm font-medium transition-colors ${activeTab === 'stations' ? 'border-b-2 border-indigo-600 text-indigo-600' : 'text-gray-500 hover:text-gray-700'}`}
                >
                    Manage Stations
                </button>
                <button
                    onClick={() => setActiveTab('schedules')}
                    className={`pb-3 px-4 text-sm font-medium transition-colors ${activeTab === 'schedules' ? 'border-b-2 border-indigo-600 text-indigo-600' : 'text-gray-500 hover:text-gray-700'}`}
                >
                    Manage Schedules
                </button>
            </div>

            {/* Feedback Messages */}
            {message && <div className="p-4 bg-green-50 text-green-700 rounded-lg">{message}</div>}
            {error && <div className="p-4 bg-red-50 text-red-700 rounded-lg flex items-center gap-2"><AlertCircle className="w-5 h-5" /> {error}</div>}

            <div className="grid md:grid-cols-3 gap-8">
                {/* Form Section */}
                <div className="md:col-span-1">
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100"
                    >
                        {activeTab === 'stations' ? (
                            <form onSubmit={handleCreateStation} className="space-y-4">
                                <h2 className="text-xl font-bold flex items-center gap-2"><MapPin className="w-5 h-5" /> Add Station</h2>
                                <input placeholder="Station Code (e.g. NYC)" className="w-full p-2 border rounded-lg" value={stationForm.stationCode} onChange={e => setStationForm({ ...stationForm, stationCode: e.target.value })} required />
                                <input placeholder="Station Name" className="w-full p-2 border rounded-lg" value={stationForm.stationName} onChange={e => setStationForm({ ...stationForm, stationName: e.target.value })} required />
                                <input placeholder="City" className="w-full p-2 border rounded-lg" value={stationForm.city} onChange={e => setStationForm({ ...stationForm, city: e.target.value })} required />
                                <input placeholder="State" className="w-full p-2 border rounded-lg" value={stationForm.state} onChange={e => setStationForm({ ...stationForm, state: e.target.value })} required />
                                <button type="submit" disabled={loading} className="w-full bg-indigo-600 text-white py-2 rounded-lg hover:bg-indigo-700 flex justify-center items-center gap-2">{loading ? 'Saving...' : <><Plus className="w-4 h-4" /> Create Station</>}</button>
                            </form>
                        ) : (
                            <form onSubmit={handleCreateSchedule} className="space-y-4">
                                <h2 className="text-xl font-bold flex items-center gap-2"><Calendar className="w-5 h-5" /> Add Schedule</h2>
                                <input placeholder="Train ID" type="number" className="w-full p-2 border rounded-lg" value={scheduleForm.trainId} onChange={e => setScheduleForm({ ...scheduleForm, trainId: e.target.value })} required />

                                <select className="w-full p-2 border rounded-lg" value={scheduleForm.sourceStationId} onChange={e => setScheduleForm({ ...scheduleForm, sourceStationId: e.target.value })} required>
                                    <option value="">Select Source</option>
                                    {stations.map(s => <option key={s.id} value={s.id}>{s.stationName} ({s.stationCode})</option>)}
                                </select>

                                <select className="w-full p-2 border rounded-lg" value={scheduleForm.destinationStationId} onChange={e => setScheduleForm({ ...scheduleForm, destinationStationId: e.target.value })} required>
                                    <option value="">Select Destination</option>
                                    {stations.map(s => <option key={s.id} value={s.id}>{s.stationName} ({s.stationCode})</option>)}
                                </select>

                                <div className="grid grid-cols-2 gap-2">
                                    <div>
                                        <label className="text-xs text-gray-500">Departure</label>
                                        <input type="datetime-local" className="w-full p-2 border rounded-lg" value={scheduleForm.departureTime} onChange={e => setScheduleForm({ ...scheduleForm, departureTime: e.target.value })} required />
                                    </div>
                                    <div>
                                        <label className="text-xs text-gray-500">Arrival</label>
                                        <input type="datetime-local" className="w-full p-2 border rounded-lg" value={scheduleForm.arrivalTime} onChange={e => setScheduleForm({ ...scheduleForm, arrivalTime: e.target.value })} required />
                                    </div>
                                </div>

                                <input placeholder="Total Seats" type="number" className="w-full p-2 border rounded-lg" value={scheduleForm.totalSeats} onChange={e => setScheduleForm({ ...scheduleForm, totalSeats: e.target.value })} required />
                                <button type="submit" disabled={loading} className="w-full bg-indigo-600 text-white py-2 rounded-lg hover:bg-indigo-700 flex justify-center items-center gap-2">{loading ? 'Saving...' : <><Plus className="w-4 h-4" /> Create Schedule</>}</button>
                            </form>
                        )}
                    </motion.div>
                </div>

                {/* List Section */}
                <div className="md:col-span-2 space-y-4">
                    <h2 className="text-xl font-bold text-gray-800">Existing Stations</h2>
                    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Code</th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">City</th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">State</th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {stations.map((s) => (
                                    <tr key={s.id}>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{s.stationCode}</td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{s.stationName}</td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{s.city}</td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{s.state}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                        {stations.length === 0 && <div className="p-8 text-center text-gray-500">No stations found.</div>}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;
